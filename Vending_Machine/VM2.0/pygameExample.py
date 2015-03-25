import pygame
from VMDataModel import VMDataModel
from pygame.locals import *

choiceNum = ""
rawCardData = ""
myModel = VMDataModel
 	
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
		


def mainScreen(inputKey):
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
	
def selectionScreen(userNum):
	# Fill background with orange
	screen = pygame.display.get_surface()
	screen.fill((255, 165, 0), (0, 0, 800, 480))
		
	# Display some text
	pygame.font.init()
	titleFont = pygame.font.Font(None, 60)
	text = titleFont.render("Welcome!", 1, (10, 10, 10))
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
	
	if userNum == K_DELETE:
		choiceNum = choiceNum[0:(len(choiceNum) - 1)]
	elif ((e.key >= K_0 and e.key <= K_9) or (e.key >= K_KP0 and e.key <= K_KP9)) and (len(choiceNum) < 2):
			choiceNum = choiceNum + str(chr(userNum))
			
	
	if len(choiceNum) == 0: 
		numText = "__"
	elif len(choiceNum) == 1:
		numText = str(choiceNum) + "_"
	else:
		numText = str(choiceNum)
		
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
	
def verifyScreen(inputKey):
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
	text = titleFont.render("ITEM NAME", 1, (10, 10, 10))
	textpos = text.get_rect()
	textpos.centerx = screen.get_rect().centerx
	textpos.centery = screen.get_rect().centery - 150
	screen.blit(text, textpos)
	
	# Display question
	text = titleFont.render("for __ points", 1, (10, 10, 10))
	textpos = text.get_rect()
	textpos.centerx = screen.get_rect().centerx
	textpos.centery = screen.get_rect().centery - 100
	screen.blit(text, textpos)
	
	# Display YES/NO
	pygame.font.init()
	titleFont = pygame.font.Font(None, 150)
	text = titleFont.render("YES     NO", 1, (10, 10, 10))
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
	
	
def swipedCard(key):
	#if key is a valid character keep it, other wise
	global rawCardData
	if key <= K_EXCLAIM  and key >= K_KP_EQUALS:
		rawCardData += chr(key)
	else :
		print self.rawCardData + "\n"
		#user = VMUser(self.rawCardData)
		user = VMUser
		rawCardData = ""
		
		if user.verified:
			user.name = self.user.name[0] + self.user.name[1::].lower()
			print("User Data" + user.toString())
			#switch to the new window
			
			
 
if __name__ == '__main__':
	SW,SH = 800, 480
	screen = pygame.display.set_mode((SW,SH))
	pygame.display.set_caption('KNW Vending Machine Software - 2015')
	
	curWindow = mainScreen
	curWindow(None)
	_quit = False
	while not _quit:
		#If 'f' is ever pressed we toggle fullscreen
		for e in pygame.event.get():
			if(e.type is KEYDOWN): 
				#these functions are for debugging purposes only 
				if e.key == K_f:	toggle_fullscreen()
				elif e.key == K_n: 	curWindow = selectionScreen
				elif e.key == K_b: 	curWindow = mainScreen
				elif e.key == K_m:	curWindow = verifyScreen
				elif e.key == K_v:	curWindow = dispenseScreen
				elif e.key == K_ESCAPE: _quit = True
				####
				
				curWindow(e.key)
			elif e.type is QUIT: _quit = True

	
