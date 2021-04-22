enum Code : char {	
	//Shared codes
	CONNECTION_CODE = '0',
	SETUP_CODE = '1',
	EXCEPTION_CODE = 'E',	
	ECHO_CODE = '>',

	//Collecting module codes
	READING_CODE = '2',

	//Testing module codes
	LOOP_UPDATE_CODE = '2',
	TRIGGERED_UPDATE_CODE = '3',
	READING_REQUEST_CODE = '4',

};