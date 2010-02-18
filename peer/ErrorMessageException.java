package peer;

import message.*;

public class ErrorMessageException extends Exception
{

public ErrorMessageException(ErrorMessage message)
{
	super(message.getErrorDescription());
}

}