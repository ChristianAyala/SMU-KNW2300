Solaris (only SPARC is supported):

	Move "librxtxSerial.so" (found in the folder corresponding to your environment) to:
		/jre/lib/[machine type]		(Example: /jre/lib/i386/)

	You must also ensure that your user is in the usergroup "lock", "uucp", and "dialout" (if the group doesn't exist, don't create one).  RXTXComm uses the /lock folder, so /lock exists (and is either the real folder, or a symbolic link to the real "lock" folder (eg: /run/lock/ or /var/lock/).  The "lock" folder might also need to have "chmod -R 777", if it is not working correctly.
