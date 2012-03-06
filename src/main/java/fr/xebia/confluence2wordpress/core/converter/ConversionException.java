package fr.xebia.confluence2wordpress.core.converter;


public class ConversionException extends Exception {

	private static final long serialVersionUID = -9168143586409161221L;

	public ConversionException(String message) {
		super(message);
	}

	public ConversionException(String message, Throwable cause) {
		super(message, cause);
	}

}