from VMBase import VMBase

class VMUser(object):
	iden = 0
	name = ""
	team = ""
	verified = False
	admin = False
	userDB = VMBase()
	balance = None
	userDB.reloadItemCSV('itemdefs.csv')
	userDB.reloadUserCSV('users.csv', 'credit.csv')
 
	def __init__(self, data):
		self.id = 0
		self.name = ""
		self.readCard(data)
		#self.userDB.printAll()
		
	def readCard(self, swipe):
		track1begin = 0
		track1end   = 0
		track2begin = 0
		track2end   = 0
		track3begin = 0
		track3end   = 0

		for ch in range(len(swipe)):
				if swipe[ch] == '%':
						track1begin = ch + 1
				elif swipe[ch] == ';':
						track2begin = ch + 1
				elif swipe[ch] == '+':
						track3begin = ch + 1
				if swipe[ch] == '?':
						if track1end == 0 and track1begin != 0:
								track1end = ch
						elif track2end == 0 and track2begin != 0:
								track2end = ch
						elif track3end == 0 and track3begin != 0:
								track3end = ch

		track1 = swipe[track1begin:track1end]
		track2 = swipe[track2begin:track2end]
		track3 = swipe[track3begin:track3end]
		if not (track1 or track2 or track3):
				print("Read Error")
				return
		_userid = ""
		_name = ""
		_surname = ""
		#check card edition
		if (not track3) or len(track3) < 8 :
				_userid = track2[len(track2)-8:len(track2)+2]
		else:
				list = track3.split("=")
				_userid = list[0]
				_surname = list[1]
				_name = list[2]
				check = track2[len(track2)-8:len(track2)+2]
				#double check id number
				if _userid != check:
						print("Card Error")
						return
		#Uncomment for Debug info
		#print("Track 1: " + track1 + "\n")
		#print("Track 2: " + track2 + "\n")
		#print("Track 3: " + track3 + "\n")
		#print("Name   : " + _name + " " + _surname)
		#print("UserID : " + _userid)

		if(len(_userid) is 8):
			userData = self.userDB.getUserData(int(_userid));
		
		if userData == None:
			print("USER NOT FOUND")
			self.verified = False
			return None
			
			
		self.verified = True
		self.iden = userData[0]
		self.name = userData[1]
		if(userData[2] == 1): 
			self.admin = True
		else:
			self.team = userData[3]
			self.balance = userData[4]

	def toString(self):
		returnString = str(self.iden) + ", " + self.name + ", " + self.team + ", " + str(self.balance) 
		if self.admin: returnString += ", is Admin"
		return returnString
		
	def dispenseItem(self, item):
		self.userDB.removeCredit(self.iden, item)
		self.userDB.printAll()
		
	def getItem(self, location):
		return self.userDB.getItem(int(location))
		
			
		
		
