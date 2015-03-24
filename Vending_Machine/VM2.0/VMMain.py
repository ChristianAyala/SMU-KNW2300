#import sys
#sys.path.insert(0, '/home/bananapi/Documents/TBO/python/')
import pygame, time
from pygame.locals import *

curWindow = None
loadingFrames = "..."
choiceNum = ""
rawCardData = ""
myModel = None
shift = False
rendered = False

	
#a function to toggle fullscreen in an X Session
#NOTE: if loaing before X Session full screen is required rendering this function useless
#good for debugging
def toggle_fullscreen():
	screen = pygame.display.get_surface()
	tmp = screen.convert()
	caption = pygame.display.get_caption()
    
	w,h = screen.get_width(),screen.get_height()
	flags = screen.get_flags()
	bits = screen.get_bitsize()
    
	pygame.display.quit()
	pygame.display.init()
    
	screen = pygame.display.set_mode((w,h),flags^FULLSCREEN,bits)
	screen.blit(tmp,(0,0))
	pygame.display.set_caption(*caption)
	
	pygame.key.set_mods(0) #HACK: work-a-round for a SDL bug??
	pygame.mouse.set_visible(False)
    
	return screen

#resets the database to clear data from a previous user		
def resetForNewUser():
	global rawCardData
	global choiceNum
	myModel.resetData()
	choiceNum = ""
	rawCardData = ""

#translates ascii code to our character and handle shifted character codes
def getChar(key):
	global shift
	
	if shift:
		shift = False
		if key == 47: return "?"
		if key == 61: return "+"
		elif key < 256: return chr(key)
		else:return "_"
	else:
		if key == 304:
			shift = True
			return False
		else:
			return pygame.key.name(key)
	'''
	if key == 5: return '%'
	elif  key >= 65 or key <= 90: 
		#this changes the key to lowercase 
		key = key + 32
	if pygame.key.name(key) == "unkown key" or pygame.key.name(key) == "left shift":
		return
	else:
		print str(key) + " : " + pygame.key.name(key)
		return pygame.key.name(key)'''

#Loading splash screen as datebase is loaded
def loadingScreen(key):
	# Fill background with orange
	screen = pygame.display.get_surface()
	screen.fill((255, 165, 0), (0, 0, 800, 480))
		
	# Display some text
	pygame.font.init()
	titleFont = pygame.font.Font(None, 60)
	text = titleFont.render("LOADING...PLEASE STAND BY", 1, (10, 10, 10))
	textpos = text.get_rect()
	textpos.centerx = screen.get_rect().centerx
	textpos.centery = screen.get_rect().centery - 200
	screen.blit(text, textpos)
	
	pygame.display.flip()
	
def mainScreen(key):
	global rendered
	
	#we only want to render the main screen once to enable fast communication the the card reader
	if not rendered:
		global rawCardData
		global choiceNum
		choiceNum = ""
		
		# Fill background with orange
		screen = pygame.display.get_surface()
		screen.fill((255, 165, 0), (0, 0, 800, 480))
			
		# Display some text
		pygame.font.init()
		titleFont = pygame.font.Font(None, 60)
		text = titleFont.render("Welcome to the KNW Vending Machine", 1, (10, 10, 10))
		textpos = text.get_rect()
		textpos.centerx = screen.get_rect().centerx
		textpos.centery = screen.get_rect().centery - 200
		screen.blit(text, textpos)
			
		swipeFont = pygame.font.Font(None, 175)
		text = swipeFont.render("Swipe Below", 1, (10, 10, 10))
		textpos = text.get_rect()
		textpos.centerx = screen.get_rect().centerx
		textpos.centery = screen.get_rect().centery
		screen.blit(text, textpos)
		
		pygame.display.flip()
		
		rendered = True
		
	elif key:
		#if key is a valid character keep it, other wise
		keyVal = getChar(key)
		if keyVal == "=":
			print "Evauluated Data: " + rawCardData
			if (myModel.setUserWithID(rawCardData)):
				print("Loaded User Data: " + myModel.toString())
				print("*****************************************")
				global curWindow 
				curWindow = selectionScreen
				rendered = False
				selectionScreen(None)
				
			rawCardData = ""
		else:
			if keyVal == "+": rawCardData = ""
			elif keyVal: rawCardData += keyVal
				

#Selection Screen for user to sleect which part they may want
#uses numpad for this process
def selectionScreen(userNum):
	# Fill background with orange
	screen = pygame.display.get_surface()
	screen.fill((255, 165, 0), (0, 0, 800, 480))
		
	# Display some text
	pygame.font.init()
	titleFont = pygame.font.Font(None, 60)
	text = titleFont.render("Welcome " + myModel.getUserName() + "!", 1, (10, 10, 10))
	textpos = text.get_rect()
	textpos.centerx = screen.get_rect().centerx
	textpos.centery = screen.get_rect().centery - 200
	screen.blit(text, textpos)
	
	text = titleFont.render("Team: " + myModel.getTeam() + " Points: " + str(myModel.getTeamCredit()), 1, (10, 10, 10))
	textpos = text.get_rect()
	textpos.centerx = screen.get_rect().centerx
	textpos.centery = screen.get_rect().centery  + 150
	screen.blit(text, textpos)
		
	swipeFont = pygame.font.Font(None, 100)
	text = swipeFont.render("Enter An Item Number", 1, (10, 10, 10))
	textpos = text.get_rect()
	textpos.centerx = screen.get_rect().centerx
	textpos.centery = screen.get_rect().centery - 100
	screen.blit(text, textpos)
	
	global choiceNum
	global curWindow 
	
	update = True
	
	if userNum == K_DELETE or userNum == K_KP_PERIOD:
		if len(choiceNum) == 0:
			curWindow = mainScreen
			resetForNewUser()
			mainScreen(None)
			update = False
			
		else: 	
			choiceNum = choiceNum[0:(len(choiceNum) - 1)]
	elif len(choiceNum) < 2:
		
		'''ONLY ENABLE THIS FOR DEBUGGING IF NEEDED
		if userNum >= K_0 and userNum <= K_9:
			choiceNum = choiceNum + pygame.key.name(userNum)'''
		if (userNum >= K_KP0 and userNum <= K_KP9):
			choiceNum = choiceNum + pygame.key.name(userNum - 208)
			
	
	if len(choiceNum) == 0: 
		numText = "__"
	elif len(choiceNum) == 1:
		numText = str(choiceNum) + "_"
	else:
		numText = str(choiceNum)

	if update:
		swipeFont = pygame.font.Font(None, 100)
		text = swipeFont.render(numText, 1, (10, 10, 10))
		textpos = text.get_rect()
		textpos.centerx = screen.get_rect().centerx
		textpos.centery = screen.get_rect().centery
		screen.blit(text, textpos)
		
		swipeFont = pygame.font.Font(None, 50)
		text = swipeFont.render("press the back button to exit", 1, (10, 10, 10))
		textpos = text.get_rect()
		textpos.centerx = screen.get_rect().centerx 
		textpos.centery = screen.get_rect().centery + 210
		screen.blit(text, textpos)
		
	
	pygame.display.flip()
	
	if (userNum == K_RETURN or userNum == K_KP_ENTER) and len(choiceNum) == 2:
		if(myModel.setItem(choiceNum)):
			curWindow = verifyScreen
			verifyScreen(None)

#allows user to verify the item they with a binary indicator (1 - YES, 2 - NO)
def verifyScreen(key):
	# Fill background with orange
	screen = pygame.display.get_surface()
	screen.fill((255, 165, 0), (0, 0, 800, 480))
		
	# Display question
	pygame.font.init()
	titleFont = pygame.font.Font(None, 50)
	text = titleFont.render("Purchase", 1, (10, 10, 10))
	textpos = text.get_rect()
	textpos.centerx = screen.get_rect().centerx
	textpos.centery = screen.get_rect().centery - 200
	screen.blit(text, textpos)
	
	# Display question
	text = titleFont.render(myModel.getItemName(), 1, (10, 10, 10))
	textpos = text.get_rect()
	textpos.centerx = screen.get_rect().centerx
	textpos.centery = screen.get_rect().centery - 150
	screen.blit(text, textpos)
	
	# Display question
	text = titleFont.render("for " + str(myModel.getItemCost()) + " points", 1, (10, 10, 10))
	textpos = text.get_rect()
	textpos.centerx = screen.get_rect().centerx
	textpos.centery = screen.get_rect().centery - 100
	screen.blit(text, textpos)
	
	# Display YES/NO
	pygame.font.init()
	titleFont = pygame.font.Font(None, 150)
	text = titleFont.render("YES  ?  NO", 1, (10, 10, 10))
	textpos = text.get_rect()
	textpos.centerx = screen.get_rect().centerx
	textpos.centery = screen.get_rect().centery + 50
	screen.blit(text, textpos)
	
	
	# Display YES/NO
	pygame.font.init()
	titleFont = pygame.font.Font(None, 75)
	text = titleFont.render("  (1)                    (2)", 1, (10, 10, 10))
	textpos = text.get_rect()
	textpos.centerx = screen.get_rect().centerx
	textpos.centery = screen.get_rect().centery + 125
	screen.blit(text, textpos)
	
	pygame.display.flip()
	
	global curWindow 
	
	if key == K_1 or key == K_KP1:	
		myModel.dispenseItem()
		resetForNewUser()
		curWindow = mainScreen
		mainScreen(None)
	elif key == K_2 or key == K_KP2:
		global choiceNum
		choiceNum = ""
		curWindow = selectionScreen
		selectionScreen(None)
	
#A short splash screen to be displayed when an item is dispensing
def dispenseScreen(inputKey):
	# Fill background with orange
	screen = pygame.display.get_surface()
	screen.fill((255, 165, 0), (0, 0, 800, 480))
		
	# Display some text
	pygame.font.init()
	titleFont = pygame.font.Font(None, 60)
	text = titleFont.render("Dispense Screen", 1, (10, 10, 10))
	textpos = text.get_rect()
	textpos.centerx = screen.get_rect().centerx
	textpos.centery = screen.get_rect().centery - 200
	screen.blit(text, textpos)
	
	pygame.display.flip()
 
 
 #our main function which handles some variable declarations and debugging features
if __name__ == '__main__':
	#setting up our X enviorment and loading screen
	SW,SH = 800, 480
	screen = pygame.display.set_mode((SW,SH))
	pygame.display.set_caption('KNW Vending Machine Software - 2015')
	
	loadingScreen(None)
	toggle_fullscreen()
	loadingScreen(None)
	
	#important and declaring heavy variables
	from VMDataModel import VMDataModel
	myModel = VMDataModel()

	#setting up the lifecycle for the GUI and some debuggin features
	#I uses curWindow to hold my lambda expressions in order to generate 
	curWindow = mainScreen
	curWindow(None)
	_quit = False
	while not _quit:
		#If 'f' is ever pressed we toggle fullscreen
		#if "esc" is ever pressed, we quit the program
		for e in pygame.event.get():
			if(e.type is KEYDOWN):
				#these functions are for debugging purposes only 
				if e.key == K_ESCAPE: _quit = True
				'''if e.key == K_f:	toggle_fullscreen()
				elif e.key == K_ESCAPE: _quit = True
				#some other key listeners usefull for debugging
				elif e.key == K_n: 	curWindow = selectionScreen
				elif e.key == K_b: 	curWindow = mainScreen
				elif e.key == K_m:	curWindow = verifyScreen
				elif e.key == K_v:	curWindow = dispenseScreen
				####'''
				
				curWindow(e.key)
			elif e.type is QUIT: _quit = True

	
#END OF FILE
