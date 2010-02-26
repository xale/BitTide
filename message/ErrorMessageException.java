package message;

public class ErrorMessageException extends RuntimeException
{

public ErrorMessageException(ErrorMessage message)
{
	super(message.getErrorDescription());
}

}