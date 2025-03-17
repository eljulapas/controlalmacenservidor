package com.almacen.controlalmacen.exception;

//Igual esta clase luego la quitamos
public class UserNotFoundException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserNotFoundException(String message) {
        super(message);
    }
}