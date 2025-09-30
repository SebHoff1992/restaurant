/**
 * Module declaration for the restaurant simulation. Defines which packages are
 * exported and accessible from outside.
 */
module restaurant {

	// Exported packages (public API of the module)
	exports restaurant;
	exports restaurant.model;
	exports restaurant.payment;
	exports restaurant.service;
	exports restaurant.util;

	// Internal payment implementations are only accessible to the service package
	exports restaurant.payment.intern to restaurant.service;
}
