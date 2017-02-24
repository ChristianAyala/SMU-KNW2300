from VMUser import VMUser
#import serial

class VMDataModel(object):
    def __init__(self):
	self.currentUser = VMUser()
	self.currentItem = None
	self.ser = serial.Serial(port='/dev/ttyUSB0', baudrate=9600, timeout=0)
	print "inited"
    
    def resetData(self):
	self.currentUser = VMUser()
	self.currentItem = None
	print "data reset for a new user"
	
    def setUser(self, cardData):
	self.currentUser = VMUser()
	return self.currentUser.readCard(cardData)
    
    def setUserWithID(self, ID):
	print "user set attempted with: " + str(ID)
	return self.currentUser.VMUserWithID(ID)
	
    def getUserName(self):
	if self.currentUser: return self.currentUser.name
	else:
	    print "no user yet" 
	    return False
	
    def getTeam(self):
	return self.currentUser.team
	
    def getTeamCredit(self):
	return self.currentUser.balance
	
    def setItem(self, location):
	self.currentItem = self.currentUser.getItem(location)
	return self.currentItem
	
    def getItemName(self):
	if self.currentItem: return self.currentItem[0]
	else: return False
    
	
    def getItemCost(self):
	if self.currentItem: return self.currentItem[1]
	else: return False
	
    def dispenseItem(self):
	if self.currentItem:
	    if not self.currentUser.dispenseItem(self.currentItem):
	    	return True
	    itemLocation = str(self.currentItem[2])
	    #padding location number for serial output
	    while not len(itemLocation) == 2:
		itemLocation = "0" + itemLocation 
	    self.ser.write("*" + itemLocation[0] + "*" + itemLocation[1] + "*")
	    return True
	else: return False
	
    def toString(self):
	return self.currentUser.toString()
	
