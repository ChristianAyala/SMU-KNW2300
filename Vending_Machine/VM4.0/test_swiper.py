from __future__ import print_function

import httplib2
import os
import io

from apiclient import discovery
from apiclient.http import MediaIoBaseDownload
from apiclient.http import MediaFileUpload
from oauth2client import client
from oauth2client import tools
from oauth2client.file import Storage
from sys import version_info

try:
    import argparse
    flags = argparse.ArgumentParser(parents=[tools.argparser]).parse_args()
except ImportError:
    flags = None

py3 = version_info[0] > 2
    
SCOPES = 'https://www.googleapis.com/auth/spreadsheets'
CLIENT_SECRET_FILE = 'vending_secret.json'
APPLICATION_NAME = 'KNW2300 Vending Machine'
spreadsheetId = '1nyUnA28ls_p6skdZ9lCIV0Jk20g604BvdxroPGd3ZHg'
teams = []
staged = []
def main():
	credentials = get_credentials()
	http = credentials.authorize(httplib2.Http())
	discoveryUrl = ('https://sheets.googleapis.com/$discovery/rest?version=v4')
	service = discovery.build('sheets', 'v4', http=http, discoveryServiceUrl=discoveryUrl)
	input_teams(service)
	# read_file(service)

def input_teams(service):
	while True:
		print('\nWhat would you like to do?\n1. Display Teams\n2. Add a team\n3. Remove a team\n4. Add Users (you can\'t return to this list after adding students)\n5. Exit\n')
		if py3:
			response = input("Enter the number of the operation: ")
		else:
			response = raw_input("Enter the number of the operation: ")	

		print('\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n')

		if response == '1':
			display_teams()
		elif response == '2':
			add_team()
		elif response == '3':
			remove_team()
		elif response == '4':
			break
		elif response == '5':
			return
		else:
			print('Please choose a valid option')
	while True:
		print('\nWhat would you like to do?\n1. Display staged users\n2. Add Admin\n3. Add Student\n4. Delete staged user\n5. Write to Drive\n6. Exit without saving\n')
		if py3:
			response = input("Enter the number of the operation: ")
		else:
			response = raw_input("Enter the number of the operation: ")	
		print('\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n')

		if response == '1':
			display_staged()
		elif response == '2':
			add_admin()
		elif response == '3':
			add_student()
		elif response == '4':
			delete_staged()
		elif response == '5':
			result = write_to_drive(service)
			if result:
				print('Thank you for inputing %d users.  They have successfully been added to the vending machine roster.' % (len(staged)))
				return
		elif response == '6':
			if py3:
				response = input("Are you sure you want to exit?(Y or N): ")
			else:
				response = raw_input("Are you sure you want to exit?(Y or N): ")	
			if response == 'Y':
				return
		else:
			print('Please choose a valid option')

def write_to_drive(service):
	body = {'values': staged}
	rangeName = 'A:E'
	result = service.spreadsheets().values().append(spreadsheetId=spreadsheetId, valueInputOption='RAW', range=rangeName, body=body, insertDataOption="INSERT_ROWS").execute()	
	return True

def delete_staged():
	display_staged()
	if py3:
		response = input("Enter the user index to delete (or EXIT to exit): ")
	else:
		response = raw_input("Enter the user index to delete (or EXIT to exit): ")
	if response != 'EXIT':
		index = int(response)
		if index < 0 or index > (len(staged)-1):
			print('not a valid index, returning to options')
			return
		staged.remove(staged[index])

def display_staged():
	print('Index  ->  Name                    ->  Team                    ')
	if len(staged) == 0:
		print("THERE ARE NO USERS INPUTTED YET")
	else:
		for i in range(len(staged)):
			print('%-7d->  %-24s->  %s' %(i, staged[i][1], staged[i][3]))
		print('\n\n\n')

def add_admin():
	if py3:
		name = input("Enter the admin name (or 0 to exit): ")
	else:
		name = raw_input("Enter the admin name (or 0 to exit): ")	

	if name == '0':
		return
	if py3:
		swipe = input("Swipe id (or 0 to exit): ")
	else:
		swipe = raw_input("Swipe id (or 0 to exit): ")	
	if swipe == '0':
		return
	staged.append([swipe.lower()[1:], name, 1, 'Admin'])

def add_student():
	if len(teams) == 0:
		print('there are no teams to add a student to.  returning to options')
		return
	if py3:
		name = input("Enter the student name (or 0 to exit): ")
	else:
		name = raw_input("Enter the student name (or 0 to exit): ")	

	if name == '0':
		return

	display_teams()
	if py3:
		teamIndex = input("Enter the index for the teamName (or EXIT to exit): ")
	else:
		teamIndex = raw_input("Enter the index for the teamName (or EXIT to exit): ")	
	if teamIndex == 'EXIT':
		return
	index = int(teamIndex)
	if index < 0 or index > (len(teams)-1):
		print('not a valid index, returning to options')
		return

	if py3:
		swipe = input("Swipe id (or 0 to exit): ")
	else:
		swipe = raw_input("Swipe id (or 0 to exit): ")	
	if swipe == '0':
		return
	staged.append([swipe.lower()[1:], name, 0, teams[index]])


def display_teams():
	print('Index  ->  Team Name')
	if len(teams) == 0:
		print("THERE ARE NO TEAMS INPUTTED YET")
	else:
		for i in range(len(teams)):
			print('%-7d->  %s' %(i, teams[i]))

def add_team():
	display_teams()
	if py3:
		response = input("Enter the new teams name (or 0 to exit): ")
	else:
		response = raw_input("Enter the new teams name (or 0 to exit): ")
	if response != '0':
		for name in teams:
			if name == response or name == 'Admin':
				print('That team name already exists')
				return
		teams.append(response)

def remove_team():
	display_teams()
	if py3:
		response = input("Enter the team index to delete (or EXIT to exit): ")
	else:
		response = raw_input("Enter the team index to delete (or EXIT to exit): ")
	if response != 'EXIT':
		index = int(response)
		if index < 0 or index > (len(teams)-1):
			print('not a valid index, returning to options')
			return
		teams.remove(teams[index])


def getInputAndWrite():
	response = input("Please enter your name: ")

def read_file(service):
	rangeName = 'A:D'
	result = service.spreadsheets().values().get(spreadsheetId=spreadsheetId, range=rangeName).execute()
	values = result.get('values', [])
	if not values:
		print('No data found.')
	else:
		print('Name, Major:')
	for row in values:
		# Print columns A and E, which correspond to indices 0 and 4.
		print('%s, %s, %s, %s' % (row[0], row[1], row[2], row[3]))

def get_credentials():
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

if __name__ == '__main__':
    main()
