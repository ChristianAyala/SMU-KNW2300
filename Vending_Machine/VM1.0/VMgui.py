import wx, sys, sched, time
#import serial
from time import sleep
from VMUser import VMUser

class VMgui(wx.Frame):
	def __init__(self, *args, **kw):
		super(VMgui, self).__init__(*args, **kw)
		self.initUI()
		rawCardData = ""
		user = None

	def initUI(self):
		#self.ser = serial.Serial(port='/dev/ttyUSB0', baudrate=9600, timeout=0)
		windowH = 480
		windowW = 800
		windowSize = wx.Size(windowW,windowH)
		largeFont = 50
		smallFont = 30

		self.swipeSizer = wx.BoxSizer(wx.VERTICAL)
		self.swipeWin = wx.Frame(None, -1, 'swipeWindow')
		self.swipeTextVal = "Please Swipe ID"
		self.swipeText = wx.StaticText(self.swipeWin, -1, self.swipeTextVal, wx.DefaultPosition, wx.DefaultSize, wx.ALIGN_CENTRE_HORIZONTAL|wx.ST_NO_AUTORESIZE)
		self.swipeText.SetFont(wx.Font(largeFont, wx.FONTFAMILY_MODERN, wx.FONTSTYLE_NORMAL, wx.FONTWEIGHT_NORMAL))
		self.swipeTextCtrl = wx.TextCtrl(self.swipeWin, -1, size=(1, 1))

		self.swipeSizer.Add(self.swipeText, 1, wx.ALIGN_CENTER_VERTICAL)
		self.swipeWin.SetWindowStyleFlag(wx.BORDER_NONE)
		self.swipeWin.SetSize(windowSize)
		self.swipeWin.CenterOnScreen()
		self.swipeWin.SetSizer(self.swipeSizer)
		self.swipeWin.SetBackgroundColour(wx.Colour(225,80,0))
	


		self.itemWin = wx.Frame(None, -1, 'itemWindow')
		self.itemSizer = wx.BoxSizer(wx.VERTICAL)
		self.itemTextVal = "Select an item:"
		self.itemText = wx.StaticText(self.itemWin, -1, self.itemTextVal, wx.DefaultPosition, wx.DefaultSize, wx.ALIGN_CENTRE_HORIZONTAL|wx.ST_NO_AUTORESIZE)
		self.itemTextCtrl = wx.TextCtrl(self.itemWin)
		self.itemText.SetFont(wx.Font(smallFont, wx.FONTFAMILY_MODERN, wx.FONTSTYLE_NORMAL, wx.FONTWEIGHT_NORMAL))
		self.itemTextCtrl.SetFont(wx.Font(smallFont, wx.FONTFAMILY_MODERN, wx.FONTSTYLE_NORMAL, wx.FONTWEIGHT_NORMAL))
		self.itemDispTextVal = "                          "
		self.itemDispText = wx.StaticText(self.itemWin, -1, self.itemDispTextVal, wx.DefaultPosition, wx.DefaultSize, wx.ALIGN_CENTRE_HORIZONTAL|wx.ST_NO_AUTORESIZE)
		self.itemDispText.SetFont(wx.Font(smallFont, wx.FONTFAMILY_MODERN, wx.FONTSTYLE_NORMAL, wx.FONTWEIGHT_NORMAL))

		self.welcomeTextVal = "Welcome "
		self.welcomeText = wx.StaticText(self.itemWin, -1, self.welcomeTextVal, wx.DefaultPosition, wx.DefaultSize, wx.ALIGN_CENTRE_HORIZONTAL|wx.ST_NO_AUTORESIZE)
		self.welcomeText.SetFont(wx.Font(largeFont, wx.FONTFAMILY_MODERN, wx.FONTSTYLE_NORMAL, wx.FONTWEIGHT_NORMAL))

		self.itemSizer.Add(self.welcomeText, 1, wx.ALIGN_CENTER)
		self.itemSizer.Add(self.itemText, 1, wx.ALIGN_CENTER)
		self.itemSizer.Add(self.itemTextCtrl, 1, wx.ALIGN_CENTER)
		self.itemSizer.Add(self.itemDispText, 1, wx.ALIGN_CENTER)

		self.itemWin.SetWindowStyleFlag(wx.BORDER_NONE)
		self.itemWin.SetSize(windowSize)
		self.itemWin.CenterOnScreen()
		self.itemWin.SetSizer(self.itemSizer)
		self.itemWin.SetBackgroundColour(wx.Colour(255, 80, 0))

		self.adminWin = wx.Frame(None, -1, 'adminWindow')
		self.adminSizer = wx.BoxSizer(wx.VERTICAL)

		self.adminTextVal = "Select an option: \n0. QUIT\n1. add/remove Team\n2. add/remove member\n3. add/remove an admin\n4. stock options\n"
		self.adminText = wx.StaticText(self.adminWin, -1, self.adminTextVal, (0, 0), wx.DefaultSize)
		self.adminTextCtrl = wx.TextCtrl(self.adminWin)
		self.adminText.SetFont(wx.Font(50, wx.FONTFAMILY_MODERN, wx.FONTSTYLE_NORMAL, wx.FONTWEIGHT_NORMAL))
		self.adminTextCtrl.SetFont(wx.Font(50, wx.FONTFAMILY_MODERN, wx.FONTSTYLE_NORMAL, wx.FONTWEIGHT_NORMAL))

		self.adminDispText = wx.StaticText(self.adminWin, -1)
		self.adminDispText.SetFont(wx.Font(18, wx.FONTFAMILY_MODERN, wx.FONTSTYLE_NORMAL, wx.FONTWEIGHT_NORMAL))

		self.adminSizer.Add(self.adminText, 2, wx.ALIGN_LEFT, wx.ALIGN_TOP)
		self.adminSizer.Add(self.adminTextCtrl, 2, wx.ALIGN_RIGHT)
		self.adminSizer.Add(self.adminDispText, 1, wx.ALIGN_RIGHT)

		self.adminWin.SetWindowStyleFlag(wx.BORDER_NONE)
		self.adminWin.SetSize(windowSize)
		self.adminWin.CenterOnScreen()
		self.adminWin.SetSizer(self.adminSizer)
		self.adminWin.SetBackgroundColour(wx.Colour(255, 80, 0))

		self.dispenseSizer = wx.BoxSizer(wx.VERTICAL)
		self.dispenseWin = wx.Frame(None, -1, 'dispenseWindow')
		self.dispenseTextVal = "Dispensing: "
		self.dispenseText = wx.StaticText(self.dispenseWin, -1, self.dispenseTextVal, wx.DefaultPosition, wx.DefaultSize, wx.ALIGN_CENTRE_HORIZONTAL|wx.ST_NO_AUTORESIZE)
		self.dispenseText.SetFont(wx.Font(largeFont, wx.FONTFAMILY_MODERN, wx.FONTSTYLE_NORMAL, wx.FONTWEIGHT_NORMAL))
		self.dispenseWin.SetWindowStyleFlag(wx.BORDER_NONE)
		self.dispenseWin.SetSize(windowSize)
		self.dispenseWin.CenterOnScreen()
		self.dispenseWin.SetSizer(self.dispenseSizer)
		self.dispenseWin.SetBackgroundColour(wx.Colour(255, 80, 0))
		self.dispenseSizer.Add(self.dispenseText, 1, wx.ALIGN_CENTER)
		
		'''self.confirmSizer = wx.BoxSizer(wx.VERTICAL)
		self.confirmWin = wx.Frame(None, -1, 'confirmWindow')
		self.confirmTextVal = "Are"
		self.confirmText = wx.StaticText(self.confirmWin, -1, self.confirmTextVal, wx.DefaultPosition, wx.DefaultSize, wx.ALIGN_CENTRE_HORIZONTAL|wx.ST_NO_AUTORESIZE)
		self.confirmText.SetFont(wx.Font(largeFont, wx.FONTFAMILY_MODERN, wx.FONTSTYLE_NORMAL, wx.FONTWEIGHT_NORMAL))
		self.confirmTextCtrl = wx.TextCtrl(self.confirmWin, -1, size=(1, 1))
		self.confirmSizer.Add(self.confirmText, 1, wx.ALIGN_CENTER_VERTICAL)
		
		#self.confirmWin.SetWindowStyleFlag()
		self.confirmWin.SetSize(windowSize)
		self.confirmWin.CenterOnScreen()
		self.confirmWin.SetSizer(self.dispenseSizer)
		self.confirmWin.SetBackgroundColour(wx.Colour(255, 80, 0))
		self.confirmSizer.Add(self.dispenseText, 1, wx.ALIGN_CENTER)'''
		
		
		
		self.swipe1Sizer = wx.BoxSizer(wx.VERTICAL)
		self.swipe1Win = wx.Frame(None, -1, 'swipeWindow')
		self.swipe1TextVal = "My Text"
		self.swipe1Text = wx.StaticText(self.swipe1Win, -1, self.swipe1TextVal, wx.DefaultPosition, wx.DefaultSize, wx.ALIGN_CENTRE_HORIZONTAL|wx.ST_NO_AUTORESIZE)
		self.swipe1Text.SetFont(wx.Font(largeFont, wx.FONTFAMILY_MODERN, wx.FONTSTYLE_NORMAL, wx.FONTWEIGHT_NORMAL))
		self.swipe1TextCtrl = wx.TextCtrl(self.swipe1Win, -1, size=(1, 1))

		self.swipe1Sizer.Add(self.swipeText, 1, wx.ALIGN_CENTER_VERTICAL)
		self.swipe1Win.SetWindowStyleFlag(wx.BORDER_NONE)
		self.swipe1Win.SetSize(windowSize)
		self.swipe1Win.CenterOnScreen()
		self.swipe1Win.SetSizer(self.swipe1Sizer)
		self.swipe1Win.SetBackgroundColour(wx.Colour(225,80,0))
		
	def promptSwipeWindow(self):
		self.itemTextCtrl.Remove(0, self.itemTextCtrl.GetLineLength(0))
		self.newUser = None
		self.swipeWin.Show()
		self.swipeTextCtrl.SetFocus()
		self.itemWin.Hide()
		#binding the entering of text with OnSwipe Function
		self.rawCardData = ""
		self.swipeTextCtrl.Bind(wx.EVT_CHAR, self.swipedCard)
		
	def swipedCard(self, event):
		key = event.GetRawKeyCode()
		print("swipe" + str(key))
		#if key is a valid character keep it, other wise
		if key <  256 and key > 13 :
			self.rawCardData += str(chr(key))
		else :
			print self.rawCardData + "\n"
			self.user = VMUser(self.rawCardData)
			self.rawCardData = ""

			#if name is valid then we set it to lower case
			if(len(self.user.name) > 1):
				self.user.name = self.user.name[0] + self.user.name[1::].lower()

			print("User Data" + self.user.toString())

			if(self.user.verified):
				self.promptSelection()

	def promptSelection(self):
		self.welcomeText.SetLabel(self.welcomeTextVal + self.user.name + "\nTeam Balance: " + str(self.user.balance) + "\nItem: ??  Cost: ??")
		print "Balance: " + str(self.user.balance)
		self.itemWin.Show()
		self.swipeWin.Hide()
		self.itemTextCtrl.Bind(wx.EVT_CHAR, self.choiceInput)
		self.itemTextCtrl.SetFocus()
		
	def choiceInput(self, event):
		key = event.GetRawKeyCode()
		if key == 13:
			if self.itemTextCtrl.GetLineLength(0) > 0:
				print self.itemTextCtrl.GetLineText(0)
				self.dispense(self.user.getItem(self.itemTextCtrl.GetLineText(0)))
				#self.verifySelection(self.user.getItem(self.itemTextCtrl.GetLineText(0)))
		elif key == 46:
			if self.itemTextCtrl.GetLastPosition() == 0:
				self.promptSwipeWindow()
			self.itemTextCtrl.Remove(self.itemTextCtrl.GetLastPosition()-1, self.itemTextCtrl.GetLastPosition())
		elif key > 47 and key < 58:
			if self.itemTextCtrl.GetLastPosition() < 2:
				self.itemTextCtrl.AppendText(chr(key))
				currItem = self.itemTextCtrl.GetLineText(0)
			
			if self.itemTextCtrl.GetLastPosition() == 2:
				item = self.user.getItem(self.itemTextCtrl.GetLineText(0))
				self.welcomeText.SetLabel(self.welcomeTextVal + self.user.name + "\nTeam Balance: " + str(self.user.balance) + "\nCost: " + str(item[1]))
			'''if len(currItem) == 2:
				self.itemDispText.SetLabel("Item: " + str(self.itemList[currItem].name) +"\nCost: " + str(self.itemList[currItem].cost) + " Qty: " + str(self.itemList[currItem].qty))
				wx.SafeYield()
                self.itemTextCtrl.SetFocus()'''
			
	def verifySelection(self, item):
		self.swipe1Win.Show()
		self.itemWin.Hide()
		#self.itemWin.Hide()
		#self.swipeTextCtrl.SetFocus()
		self.dispense(item)
		if item == None:
			#self.confirmTextSetLabel("Your Item Was Not Found")
			#set text to failure to find time
			print "ITEM NOT FOUND"
		else:
			print ", ".join(map(str, item))
			#self.confirmText.SetLabel("Are")
			#ask user -> "Are you sure you want a ITEMNAME?"
			#0 - no 
			#1 - yes
			#press any other button to cancel
			#either go back tp main screen or go the dispense screen
			#self.dispense(item)
			
	def dispense(self, item):	
		itemName = item[0]
		itemLocation = item[2]
		self.dispenseText.SetLabel(self.dispenseTextVal + str(itemName))
		wx.Yield()
		self.itemWin.Hide()
		wx.Yield()
		self.dispenseWin.Show()
		
		wx.Yield()
		#self.ser.write("*" + itemLocation[0] + "*" + itemLocation[1] + "*")
		self.user.dispenseItem(item)
		sleep(3)
		self.dispenseWin.Hide()
		self.promptSwipeWindow()
