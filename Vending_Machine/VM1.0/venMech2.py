import wx, time, sys, sqlite3
from collections import namedtuple

class VendingMachine(wx.Frame):
        def __init__(self, *args, **kw):
                super(VendingMachine, self).__init__(*args, **kw)
                self.conn = sqlite3.connect('fyd_user_base.db')
                self.c = self.conn.cursor()
                self.keyDict = {384:'0', 382:'1', 379:'2', 381:'3', 376:'4', 383:'5', 378:'6', 375:'7', 377:'8', 380:'9'}
                self.initUI()


        def initUI(self):
                #self.ser = serial.Serial(port='/dev/ttyUSB0', baudrate=9600, timeout=0)
                windowH = 234
                windowW = 438
                windowSize = wx.Size(windowW,windowH)
                largeFont = 32
                smallFont = 18

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
                self.itemDispText.SetFont(wx.Font(18, wx.FONTFAMILY_MODERN, wx.FONTSTYLE_NORMAL, wx.FONTWEIGHT_NORMAL))

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
                self.adminText.SetFont(wx.Font(smallFont, wx.FONTFAMILY_MODERN, wx.FONTSTYLE_NORMAL, wx.FONTWEIGHT_NORMAL))
                self.adminTextCtrl.SetFont(wx.Font(smallFont, wx.FONTFAMILY_MODERN, wx.FONTSTYLE_NORMAL, wx.FONTWEIGHT_NORMAL))

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


                self.User = namedtuple('User', ['name', 'surname', 'id'], verbose=False)

        def promptSwipe(self):
                self.User.id = ""
                self.User.name = ""
                self.User.surname = ""
                self.cardString = ""

                #displaying the intial swipe window
                self.swipeWin.Show()

                #setting focus to a text controller to recieve swipe text
                self.swipeTextCtrl.SetFocus()

                #hiding the item window
                self.itemWin.Hide()

                #binding the entering of text with OnSwipe Function
                self.swipeTextCtrl.Bind(wx.EVT_CHAR, self.OnSwipe)

        def promptItem(self):
                self.welcomeText.SetLabel(self.welcomeTextVal + self.User.name)
                self.itemWin.Show()
                self.swipeWin.Hide()
                self.itemTextCtrl.Bind(wx.EVT_CHAR, self.OnEntry)
                self.itemTextCtrl.SetFocus()

        def promptAdmin(self):
                #self.welcomeText.SetLabel(self.adminWelcomeTextVal + self.User.name)
                self.adminWin.Show()
                self.swipeWin.Hide()
                self.itemTextCtrl.Bind(wx.EVT_CHAR, self.OnEntry)
                self.itemTextCtrl.SetFocus()


        def readCard(self):
                swipe = self.cardString

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


                self.User.name = _name
                self.User.surname = _surname
                self.User.id = _userid

        def OnSwipe(self, event):
                
                key = event.GetRawKeyCode()
                print("swipe" + str(key))
                #if key is a valid character keep it, other wise
                if key <  256 and key > 13 :
                        self.cardString += str(chr(key))
                else :
                        
                        print self.cardString + "\n"
                        self.readCard()
                        self.cardString = ""

                        #if name is valid then we set it to lower case
                        if(len(self.User.name) > 1):
                                self.User.name = self.User.name[0] + self.User.name[1::].lower()

                        #if we have a valid user ID of 8 characters then we setup item prompting
                        
                        #checking for admin privs
                        t = (int(self.User.id),)
                        self.c.execute('SELECT admin FROM users WHERE id=?', t)
                        if(len(self.User.id) is 8):
                                if (self.c.fetchone()[0]):
                                        self.promptItem()
                                else:
                                        self.promptItem()
                #event.Skip()



        def OnEntry(self, event):

                
                key = event.GetUnicodeKey()
                print str(key)
                if(key == 370):
                        if self.itemTextCtrl.GetLineLength(0) == 2:
                                itemNum = self.itemTextCtrl.GetLineText(0)
                                try:
                                        itemNum = str(itemNum)
                                        self.itemTextCtrl.Remove(0, self.itemTextCtrl.GetLastPosition())
                                        wx.Yield()
                                        self.dispense(itemNum)
                                        self.promptSwipe()
                                except TypeError:
                                        print("Type Error")
                                except Exception as e:
                                        print(e)
                elif key == 385:
                        self.itemTextCtrl.Remove(self.itemTextCtrl.GetLastPosition()-1, self.itemTextCtrl.GetLastPosition())
                elif key in self.keyDict:
                        self.itemTextCtrl.AppendText(self.keyDict[key])
                        currItem = self.itemTextCtrl.GetLineText(0)
                        if len(currItem) == 2:
                                try:
                                        self.itemDispText.SetLabel("Item: " + str(self.itemList[currItem].name) +"\nCost: " + str(self.itemList[currItem].cost) + " Qty: " + str(self.itemList[currItem].qty))
                                        wx.SafeYield()
                                        self.itemTextCtrl.SetFocus()
                                except KeyError as e:
                                        print "Key out of bounds\n"
                if self.itemTextCtrl.GetLineLength(0) < 2:
                        self.itemDispText.SetLabel("")
                #event.Skip()


        # getBalanceByID
        # This function uses the user id to look up the balance available to the user
        #def getBalanceByID(self, id)
        #       self.id #this is the id number

        # setBalanceByID
        # This function uses the user id to update the balance available to the user
        #def setBalanceByID(self, id, balance)
        #       self.id #this is the id number

        def dispense(self, itemNum):
                self.dispenseText.SetLabel(self.dispenseTextVal + str(itemNum))
                wx.Yield()
                self.itemWin.Hide()
                wx.Yield()
                self.dispenseWin.Show()

                wx.Yield()
                #self.ser.write("*" + itemNum[0] + "*" + itemNum[1] + "*")
                self.Update()
                wx.Yield()
                while True:
                        wx.Yield()
                        x = self.ser.read(6)
                        if len(x) != 0:
                                break

                wx.Yield()
                self.dispenseWin.Hide()
                wx.Yield()

                self.itemDispText.SetLabel("")


def main():
        app = wx.App()
        vm = VendingMachine(None)
        vm.promptSwipe()

        app.MainLoop()
        conn.close()

if __name__ == '__main__':
        main()
