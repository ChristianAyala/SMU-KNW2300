from __future__ import print_function
import sqlite3
import csv

class VMBase(object):
	conn = None
	c = None

    # The class "constructor" - It's actually an initializer 
	def __init__(self):
		self.conn = sqlite3.connect('fyd_user_base.db')
		self.c = self.conn.cursor()

	def CREATE_TABLES(self):
		self.c.execute("CREATE TABLE users (id integer, name text, admin integer, team text)")
		self.c.execute("CREATE TABLE credit (team_name text, credits int)")
		self.c.execute("CREATE TABLE items (name text, cost int, location int, stock int)")
		self.conn.commit()
		
	def DELETE_TABLES(self):
		self.c.execute("DELETE FROM users")
		self.c.execute("DELETE FROM credit")
		self.c.execute("DELETE FROM items")
		self.conn.commit()
		
	def reloadItemCSV(self, fileName):
		#first we must remove all data from the table
		self.c.execute("DELETE FROM items")
		self.conn.commit()
		#CSV file should be defined as {name,cost,location,stock} with the first line being ignored
		with open(fileName, 'rb') as csvfile:
			data = list(csv.reader(csvfile, delimiter=',', quotechar='\''))
			for row in data[1:]:
				#print(",".join(row))
				name = row[0].strip()
				cost = int(row[1].strip())
				loc = int(row[2].strip())
				stock = int(row[3].strip())
				#print ", ".join(row) #quick way to print input for debugging
				self.addItem(name, cost, loc, stock)
	
	
	def reloadUserCSV(self, fileName):
		#first we must remove all data from the table
		self.c.execute("DELETE FROM users")
		self.c.execute("DELETE FROM credit")
		self.conn.commit()
		#CSV file should be defined as {ID,name,admin,group} with the first line being ignored
		with open(fileName, 'rb') as csvfile:
			data = list(csv.reader(csvfile, delimiter=',', quotechar='\''))
			groups = []
			allowance = int(data[0][5])
			for row in data[1:]:
				iden = int(row[0].strip())
				name = row[1].strip()
				admin = int(row[2].strip())
				team = row[3].strip()
				self.addUser(iden, name, admin, team)
				if team not in groups and not admin: 
					self.addTeam(team, allowance)
					groups.append(team)
					
	def reloadUserCSV(self, userFile, creditFile):
		#first we must remove all data from the table
		self.c.execute("DELETE FROM users")
		self.c.execute("DELETE FROM credit")
		self.conn.commit()
		#CSV file should be defined as {ID,name,admin,group} with the first line being ignored
		with open(userFile, 'rb') as csvfile:
			data = list(csv.reader(csvfile, delimiter=',', quotechar='\''))
			for row in data[1:]:
				self.addUser(int(row[0].strip()), row[1].strip(), int(row[2].strip()), row[3].strip())
		with open(creditFile, 'rb') as csvfile:
			data = list(csv.reader(csvfile, delimiter=',', quotechar='\''))
			for row in data[1:]:
				self.addTeam(row[0].strip(), int(row[1].strip()))
				
				


	def addItem(self, name, cost, location, stock):
		self.c.execute("INSERT INTO items VALUES ('%s',%d,%d,%d)" % (name, cost, location, stock))
		self.conn.commit()
		
	def getItem(self, location):
		return self.c.execute("SELECT * FROM items WHERE location=%d" % int(location)).fetchone()
		
	def addUser(self, iden, name, admin, group):
		adminNum = 0
		if admin:adminNum = 1
		self.c.execute("INSERT INTO users VALUES (%d,'%s',%d,'%s')" % (iden, name, adminNum, group))
		self.conn.commit()
		
			
	def addTeam(self, name, credit):
		self.c.execute("INSERT INTO credits VALUES (%s, '%d')" % (name, credit))
		self.conn.commit()
		
	def getUserData(self, iden):
		dat = self.c.execute("SELECT * FROM users WHERE id=%d" % iden).fetchone()
		if not dat: return None
		if dat[2] == 1:	return dat
		else: return dat + (self.c.execute(("SELECT credits FROM credit WHERE team_name='%s'" % dat[3])).fetchone()[0],)
		
				
	def addTeam(self, teamName, credit):
		self.c.execute("INSERT INTO credit VALUES ('%s', %d)" % (teamName, credit))
		self.conn.commit()
		
	def removeCredit(self, iden, item):
		removeCredit = item[1]
		if int(self.c.execute("SELECT admin FROM users WHERE id=%d" % iden).fetchone()[0]) == 1:
			return True #the user is an admin therefore there is no need to remove credit
		else:
			teamName = self.c.execute("SELECT team FROM users WHERE id=%d" % iden).fetchone()[0]
			oldCredit = self.c.execute("SELECT credits FROM credit WHERE team_name='%s'" % teamName).fetchone()[0]
			if oldCredit < removeCredit:return False
			newCredit = oldCredit - removeCredit
			self.c.execute("UPDATE credit SET credits = %d WHERE team_name = '%s'" % (newCredit, teamName))
			self.conn.commit()
			self.printToCSV("credit.csv")
			return True
		
	def printToCSV(self, creditCSV):
		with open(creditCSV, "w") as f:
			print("Team,Credit", file=f)
			self.c.execute("SELECT * FROM credit")
			all_rows = self.c.fetchall()
			for row in all_rows:
				print("%s,%d" % (row[0], row[1]), file=f)
		
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
