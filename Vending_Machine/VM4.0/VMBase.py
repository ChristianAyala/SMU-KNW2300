from __future__ import print_function
import sqlite3
import csv

from threading import Thread
import time

import httplib2
import os
import io

from apiclient import discovery
from apiclient.http import MediaIoBaseDownload
from apiclient.http import MediaFileUpload
from oauth2client import client
from oauth2client import tools
from oauth2client.file import Storage

try:
    import argparse
    flags = argparse.ArgumentParser(parents=[tools.argparser]).parse_args()
except ImportError:
    flags = None
    
SCOPES = 'https://www.googleapis.com/auth/drive'
CLIENT_SECRET_FILE = 'vending_secret.json'
APPLICATION_NAME = 'KNW2300 Vending Machine'
THREAD_DELAY = 5 # # of seconds in 15 minutes
ITEMS_FILENAME = 'VM_Items.csv'
USERS_FILENAME = 'VM_Users.csv'
CREDIT_FILENAME = 'VM_Credit.csv'

class VMBase(object):
	conn = None
	c = None
	driveService = None
	running = False
	changeUsers = False
	changeItems = False
	changeCredit = False

    # The class "constructor" - It's actually an initializer 
	def __init__(self):
		self.conn = sqlite3.connect('fyd_user_base.db' )
		self.c = self.conn.cursor()
		credentials = self.get_credentials()
		http = credentials.authorize(httplib2.Http())
		self.driveService = discovery.build('drive', 'v3', http=http)
		# self.DELETE_TABLES()
		# self.CREATE_TABLES()

	def start(self):
		self.saved_start_page_token = self.driveService.changes().getStartPageToken().execute().get('startPageToken')
		self.running = True
		Thread(target=self.run).start()

	def terminate(self):
		self.running=False

	def run(self):
		while self.running:
			time.sleep(5)
			page_token = self.saved_start_page_token;
			while page_token is not None:
				response = self.driveService.changes().list(pageToken=page_token, spaces='drive').execute()
				for change in response.get('changes'):
					if change.get('fileId') == self.usersFileId:
						# self.changeUsers = True
						print("change found for users file")
						self.pullUsersFromDrive()
						self.reloadUserCSV()
					elif change.get('fileId') == self.itemsFileId:
						# self.changeItems = True
						print("change found for items file")
						self.pullItemsFromDrive()
						self.reloadItemCSV()
					elif change.get('fileId') == self.creditFileId:
						# self.changeCredit = True
						print("change found for credit file")
						self.pullCreditFromDrive()
						self.reloadCreditCSV()
				if 'newStartPageToken' in response:
					self.saved_start_page_token = response.get('newStartPageToken')
				page_token = response.get('nextPageToken')

	def update(self):
		# changed = False
		# if self.changeCredit:
		self.pullCreditFromDrive()
		self.reloadCreditCSV()
		# changed = True
			# self.changeCredit = False
		# if self.changeUsers:
		self.pullUsersFromDrive()
		self.reloadUserCSV()
		# changed = True
		# 	self.changeUsers = False
		# if self.changeItems:
		self.pullItemsFromDrive()
		self.reloadItemCSV()
		# changed = True
			# self.changeItems = False
		# return changed

	def get_credentials(self):
	    home_dir = os.path.expanduser('~')
	    credential_dir = os.path.join(home_dir, '.credentials')
	    if not os.path.exists(credential_dir):
	        os.makedirs(credential_dir)
	    credential_path = os.path.join(credential_dir, 'vending_machine.json')

	    store = Storage(credential_path)
	    credentials = store.get()
	    if not credentials or credentials.invalid:
	        flow = client.flow_from_clientsecrets(CLIENT_SECRET_FILE, SCOPES)
	        flow.user_agent = APPLICATION_NAME
	        if flags:
	            credentials = tools.run_flow(flow, store, flags)
	        else:
	            credentials = tools.run(flow, store)
	    return credentials

	def findSheet(self, sheetName):
		page_token = None
		while True:
			response = self.driveService.files().list(
	                q='mimeType=\'application/vnd.google-apps.spreadsheet\' and name contains \'{:s}\''.format(sheetName),
	                spaces='drive',
	                fields='nextPageToken, files(id, name)',
	                pageToken=page_token).execute()
			for f in response.get('files', []):
				if sheetName in f.get('name'):
					return f
			page_token = response.get('nextPageToken', None)
			if page_token is None:
				break
		return None

	def pullUsersFromDrive(self):
		filePrefix = USERS_FILENAME.rsplit(".", 1)[0]
		usersFile = self.findSheet(filePrefix)
		self.usersFileId = usersFile.get('id')
		if usersFile == None:
			return
		request = self.driveService.files().export_media(fileId=usersFile.get('id'), mimeType='text/csv')
		fh = io.FileIO(USERS_FILENAME, 'wb')
		downloader = MediaIoBaseDownload(fh, request)
		done = False
		while done is False:
			status, done = downloader.next_chunk()

	def pullItemsFromDrive(self):
		filePrefix = ITEMS_FILENAME.rsplit(".", 1)[0]
		itemsFile = self.findSheet(filePrefix)
		self.itemsFileId = itemsFile.get('id')
		if itemsFile == None:
			return
		request = self.driveService.files().export_media(fileId=itemsFile.get('id'), mimeType='text/csv')
		fh = io.FileIO(ITEMS_FILENAME, 'wb')
		downloader = MediaIoBaseDownload(fh, request)
		done = False
		while done is False:
			status, done = downloader.next_chunk()

	def pullCreditFromDrive(self):
		filePrefix = CREDIT_FILENAME.rsplit(".", 1)[0]
		creditFile = self.findSheet(filePrefix)
		self.creditFileId = creditFile.get('id')
		if creditFile == None:
			return
		request = self.driveService.files().export_media(fileId=creditFile.get('id'), mimeType='text/csv')
		fh = io.FileIO(CREDIT_FILENAME, 'wb')
		downloader = MediaIoBaseDownload(fh, request)
		done = False
		while done is False:
			status, done = downloader.next_chunk()

	def reloadUserCSV(self):
		#first we must remove all data from the table
		self.c.execute("DELETE FROM users")
		# self.c.execute("DELETE FROM credit")
		self.conn.commit()
		#CSV file should be defined as {ID,name,admin,group} with the first line being ignored
		with open(USERS_FILENAME, 'rU') as csvfile:
			data = list(csv.reader(csvfile, delimiter=',', quotechar='\''))
			for row in data[1:]:
				self.addUser(row[0].strip(), row[1].strip(), int(row[2].strip()), row[3].strip())
		# with open(CREDIT_FILENAME, 'rU') as csvfile:
		# 	data = list(csv.reader(csvfile, delimiter=',', quotechar='\''))
		# 	for row in data[1:]:
		# 		self.addTeam(row[0].strip(), int(row[1].strip()))

	def reloadCreditCSV(self):
		self.c.execute("DELETE FROM credit")
		self.conn.commit()
		with open(CREDIT_FILENAME, 'rU') as csvfile:
			data = list(csv.reader(csvfile, delimiter=',', quotechar='\''))
			for row in data[1:]:
				self.addTeam(row[0].strip(), int(row[1].strip()))
		
	def reloadItemCSV(self):
		#first we must remove all data from the table
		self.c.execute("DELETE FROM items")
		self.conn.commit()
		#CSV file should be defined as {name,cost,location,stock} with the first line being ignored
		with open(ITEMS_FILENAME, 'rU') as csvfile:
			data = list(csv.reader(csvfile, delimiter=',', quotechar='\''))
			for row in data[1:]:
				#print(",".join(row))
				name = row[0].strip()
				cost = int(row[1].strip())
				loc = int(row[2].strip())
				stock = int(row[3].strip())
				#print ", ".join(row) #quick way to print input for debugging
				self.addItem(name, cost, loc, stock)

	def CREATE_TABLES(self):
		self.c.execute("CREATE TABLE users (id text, name text, admin integer, team text)")
		self.c.execute("CREATE TABLE credit (team_name text, credits int)")
		self.c.execute("CREATE TABLE items (name text, cost int, location int, stock int)")
		self.conn.commit()
		
	def DELETE_TABLES(self):
		self.c.execute("DROP TABLE users")
		self.c.execute("DROP TABLE credit")
		self.c.execute("DROP TABLE items")
		self.conn.commit()

	def scrub(self, data):
		return ''.join( chr for chr in data if chr.isalnum() or chr == ' ')

	def addItem(self, name, cost, location, stock):
		self.c.execute("INSERT INTO items VALUES (\'%s\',%d,%d,%d)" % (self.scrub(name), cost, location, stock))
		self.conn.commit()
		
	def getItem(self, location):
		return self.c.execute("SELECT * FROM items WHERE location=%d" % int(location)).fetchone()
		
	def addUser(self, iden, name, admin, group):
		adminNum = 0
		if admin:adminNum = 1
		self.c.execute("INSERT INTO users VALUES (\'%s\',\'%s\',%d,\'%s\')" % (self.scrub(iden), self.scrub(name), adminNum, self.scrub(group)))
		self.conn.commit()
		
			
	def addTeam(self, name, credit):
		self.c.execute("INSERT INTO credit VALUES (\'%s\', %d)" % (self.scrub(name), credit))
		self.conn.commit()
		
	def getUserData(self, iden):
		dat = self.c.execute("SELECT * FROM users WHERE id=\'%s\'" % self.scrub(iden)).fetchone()
		if not dat: return None
		val = None
		if dat[2] == 1:	return dat
		else: return dat + (self.c.execute(("SELECT credits FROM credit WHERE team_name=\'%s\'" % self.scrub(dat[3]))).fetchone()[0],)
		
	def removeCredit(self, iden, item):
		removeCredit = item[1]
		stock = item[3]
		if (stock - 1) < 0:
			return False
		self.c.execute("UPDATE items SET stock = stock - 1 WHERE location=%d" % (item[2]))
		print("just finished updating stock")
		if int(self.c.execute("SELECT admin FROM users WHERE id=\'%s\'" % self.scrub(iden)).fetchone()[0]) == 1:
			self.printItemsToCSV()
			self.writeItemsToDrive()
			return True #the user is an admin therefore there is no need to remove credit
		else:
			teamName = self.c.execute("SELECT team FROM users WHERE id=\'%s\'" % self.scrub(iden)).fetchone()[0]
			oldCredit = self.c.execute("SELECT credits FROM credit WHERE team_name=\'%s\'" % self.scrub(teamName)).fetchone()[0]
			if oldCredit < removeCredit:return False
			newCredit = oldCredit - removeCredit
			self.c.execute("UPDATE credit SET credits = %d WHERE team_name = \'%s\'" % (newCredit, self.scrub(teamName)))
			self.conn.commit()
			self.printToCSV()
			self.printItemsToCSV
			self.writeToDrive()
			self.writeItemsToDrive()
			return True
		
	def printToCSV(self):
		with open(CREDIT_FILENAME, "w") as f:
			print("Team,Credit", file=f)
			self.c.execute("SELECT * FROM credit")
			all_rows = self.c.fetchall()
			for row in all_rows:
				print("%s,%d" % (row[0], row[1]), file=f)

	def printItemsToCSV(self):
		with open(ITEMS_FILENAME, "w") as f:
			print("ItemName,Cost,Location,Stock",file=f)
			self.c.execute("SELECT * FROM items")
			all_rows = self.c.fetchall()
			for row in all_rows:
				print("%s,%d,%d,%d" % (row[0], row[1], row[2], row[3]), file=f)

	def writeItemsToDrive(self):
		itemsPrefix = ITEMS_FILENAME.rsplit(".", 1)[0]
		itemsFile = self.findSheet(itemsPrefix)
		file_metadata = {
			'name' : itemsPrefix,
			'mimeType' : 'application/vnd.google-apps.spreadsheet'
		}

		media = MediaFileUpload(ITEMS_FILENAME,
								mimetype='text/csv',
								resumable=True)
		if itemsFile != None:
			self.driveService.files().update(fileId=itemsFile.get('id'),
											body=file_metadata,
											media_body=media).execute()

	def writeToDrive(self):
		creditPrefix = CREDIT_FILENAME.rsplit(".", 1)[0]
		creditFile = self.findSheet(creditPrefix)

		file_metadata = {
			'name' : creditPrefix,
			'mimeType' : 'application/vnd.google-apps.spreadsheet'
		}

		media = MediaFileUpload(CREDIT_FILENAME,
								mimetype='text/csv',
								resumable=True)
		if creditFile != None:
			self.driveService.files().update(fileId=creditFile.get('id'),
											body=file_metadata,
											media_body=media).execute()

	def printAll(self):
		print("====================USERS========================")
		self.c.execute("SELECT * FROM users")
		all_rows = self.c.fetchall()
		for row in all_rows:
			print("%d, %s, %d, %s" % (row[0], row[1], row[2], row[3]))
			
		print("====================CREDITS========================")
		self.c.execute("SELECT * FROM credit")
		all_rows = self.c.fetchall()
		for row in all_rows:
			print("%s, %d" % (row[0], row[1]))
			
		print("====================ITEMS========================")
		self.c.execute("SELECT * FROM items")
		all_rows = self.c.fetchall()
		for row in all_rows:
			print("%s, %d, %d, %d" % (row[0], row[1], row[2], row[3]))
		
	def close(self):
		self.conn.close()
	
'''#Example of using the database
myDB = VMBase()
myDB.DELETE_TABLES() # THIS SHOULD ONLY BE CALLED TO COMPLETELY CLEAR THE DATABASE
myDB.addTeam('TA', 200)
myDB.addUser(35277636, 'Raymond', 1, 'TA')
myDB.addItem("servo", 20, 1, 1)
if not myDB.removeCredit(35277636, 20): #we use the if statement to check if we have enough credit
	print("not enough credit")
myDB.printAll()
myDB.close(); # be sure to close the connection to ensure data is not corrupted

#example of using that database with modifiable csv files
myDB = VMBase()
myDB.reloadItemCSV('itemdefs.csv')
myDB.reloadUserCSV('users.csv', 'credit.csv')
myDB.printAll()
myDB.removeCredit(35277636, 20)
myDB.removeCredit(11111111, 20)
myDB.printAll()
myDB.printToCSV("credit.csv")
'''
