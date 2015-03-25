import sqlite3

#Connecting to a database*******************************************
conn = sqlite3.connect('fyd_user_base.db')

c = conn.cursor()

#Creating your database and tables**********************************
c.execute('''CREATE TABLE users
	(id integer, name text, admin integer, team text)''')
	
c.execute('''CREATE TABLE credit
	(team_name text, credits int)''')

# Insert a row of data
c.execute("INSERT INTO users VALUES (35277636,'Raymond',1,'TA/FAC')")


"""
#using secure python variables**************************************
# Never do this -- insecure!
symbol = 'RHAT'
c.execute("SELECT * FROM stocks WHERE symbol = '%s'" % symbol)

# Do this instead
t = ('RHAT',)
c.execute('SELECT * FROM stocks WHERE symbol=?', t)
print c.fetchone()

# Larger example that inserts many records at a time
purchases = [('2006-03-28', 'BUY', 'IBM', 1000, 45.00),
             ('2006-04-05', 'BUY', 'MSFT', 1000, 72.00),
             ('2006-04-06', 'SELL', 'IBM', 500, 53.00),
            ]
c.executemany('INSERT INTO stocks VALUES (?,?,?,?,?)', purchases)


#getting all items in a select statement***************************
for row in c.execute('SELECT * FROM stocks ORDER BY price'):
        print row
"""

#disconnecting from server******************************************
# Save (commit) the changes
conn.commit()

# We can also close the connection if we are done with it.
# Just be sure any changes have been committed or they will be lost.
conn.close()

