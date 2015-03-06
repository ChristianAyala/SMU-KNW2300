import sys
sys.path.insert(0, '/home/bananapi/Documents/TBO/python/')

from VMDataModel import VMDataModel
import pygame, time
from pygame.locals import *

curWindow = None
loadingFrames = "..."
choiceNum = ""
rawCardData = ""
myModel = VMDataModel()
shift = False
#%0023616111102264785125876?;01543764206658850=08435277636?+35277636=MARTIN=RAYMOND=STUDENT?

	
	
	

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
		
def resetForNewUser():
	global rawCardData
	global choiceNum
	myModel.resetData()
	choiceNum = ""
	rawCardData = ""

def getChar(key):
	global shift
	
	if shift:
		shift = False
		if key == 47:
			return "?"
		else:
			return chr(key - 16)
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


def mainScreen(key):
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
	
	#if key is a valid character keep it, other wise
	if key:
		keyVal = getChar(key)
		if not key == 271:
			if keyVal: rawCardData += keyVal
		else:
			print rawCardData
			
			#%0023616111102264785125876?;01543764206658850=08435277636?+35277636=MARTIN=RAYMOND=STUDENT?
			
			
			
			'''print rawCardData + "\n"
			#user = VMUser(self.rawCardData)
			#this function is for debugging purposes
			if (myModel.setUserWithID("35277636")):
				rawCardData = ""
				print("User Data: " + myModel.toString())

				global curWindow 
				curWindow = selectionScreen
				selectionScreen(None)
				#switch to the new window'''
			
			if (myModel.setUser(rawCardData)):
				print("User Data: " + myModel.toString())
				print("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++")
				global curWindow 
				curWindow = selectionScreen
				selectionScreen(None)
				
			rawCardData = ""
			
	
	pygame.display.flip()
	
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
		
		swipeFont = pygame.font.Font(None, 20)
		text = swipeFont.render("press the back button to exit", 1, (10, 10, 10))
		textpos = text.get_rect()
		textpos.centerx = screen.get_rect().centerx 
		textpos.centery = screen.get_rect().centery + 220
		screen.blit(text, textpos)
		
	
	pygame.display.flip()
	
	if (userNum == K_RETURN or userNum == K_KP_ENTER) and len(choiceNum) == 2:
		if(myModel.setItem(choiceNum)):
			curWindow = verifyScreen
			verifyScreen(None)
	
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
	

#IN PROGRESS LOADING SCREEN
'''def loadingScreen(time):
	global loadingFrames
	# Fill background with orange
	screen = pygame.display.get_surface()
	screen.fill((255, 165, 0), (0, 0, 800, 480))
		
	# Display some text
	pygame.font.init()
	titleFont = pygame.font.Font(None, 200)
	if len(loadingFrames) > 5: loadingFrames = ""
	else: loadingFrames += "."
	print loadingFrames
	text = titleFont.render(loadingFrames, 1, (10, 10, 10))
	textpos = text.get_rect()
	textpos.centerx = screen.get_rect().centerx
	textpos.centery = screen.get_rect().centery
	screen.blit(text, textpos)
	
	pygame.display.flip()
	time.sleep(.1)
	if(time > 0):
		loadingScreen(time - 1)'''
 
if __name__ == '__main__':
	SW,SH = 800, 480
	screen = pygame.display.set_mode((SW,SH))
	pygame.display.set_caption('KNW Vending Machine Software - 2015')
	
	'''sampleCard = "%0023616111102264785125876?;01543764206658850=08435277636?+35277636=MARTIN=RAYMOND=STUDENT?"
	for i in sampleCard:
		print getChar(ord(i)) + " : " + str(ord(i))'''
	#print str(ord('a') - ord('A'))

	curWindow = mainScreen
	curWindow(None)
	_quit = False
	while not _quit:
		#If 'f' is ever pressed we toggle fullscreen
		for e in pygame.event.get():
			if(e.type is KEYDOWN):
				#these functions are for debugging purposes only 
				if e.key == K_f:	toggle_fullscreen()
				elif e.key == K_ESCAPE: _quit = True
				'''elif e.key == K_n: 	curWindow = selectionScreen
				elif e.key == K_b: 	curWindow = mainScreen
				elif e.key == K_m:	curWindow = verifyScreen
				elif e.key == K_v:	curWindow = dispenseScreen
				####'''
				
				curWindow(e.key)
			elif e.type is QUIT: _quit = True

	
