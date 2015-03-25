from VMgui import VMgui
import wx

app = wx.App()
vm = VMgui(None)
vm.promptSwipeWindow()


app.MainLoop()
conn.close()
