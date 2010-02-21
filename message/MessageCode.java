package message;

public enum MessageCode
{
	InvalidMessageCode			((byte)0),
	SuccessMessageCode			((byte)1),
	LoginMessageCode			((byte)2),
	SearchRequestMessageCode	((byte)3),
	SearchReplyMessageCode		((byte)4),
	FileInfoMessageCode			((byte)5),
	FileBitmapMessageCode		((byte)6),
	LogoutRequestMessageCode	((byte)7),
	ErrorMessageCode			((byte)8),
	FileRequestMessageCode		((byte)9),
	FileReplyMessageCode		((byte)10),
	LogoutCompleteMessageCode	((byte)11);
	
	private final byte code;

MessageCode(byte c)
{
	code = c;
}

public byte getCode()
{
	return code;
}

/**
* "Enum factory": returns the appropriate MessageCode for the specified byte.
* @param firstByte the first byte of a received message
*/
public static MessageCode messageCodeFromFirstByte(byte firstByte)
{
	switch (firstByte)
	{
		case 1:
			return SuccessMessageCode;
		case 2:
			return LoginMessageCode;
		case 3:
			return SearchRequestMessageCode;
		case 4:
			return SearchReplyMessageCode;
		case 5:
			return FileInfoMessageCode;
		case 6:
			return FileBitmapMessageCode;
		case 7:
			return LogoutRequestMessageCode;
		case 8:
			return ErrorMessageCode;
		case 9:
			return FileRequestMessageCode;
		case 10:
			return FileReplyMessageCode;
		case 11:
			return LogoutCompleteMessageCode;
		default:
			break;
	}
	
	return InvalidMessageCode;
}

}