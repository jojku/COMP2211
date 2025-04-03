package uk.ac.soton.comp2211.controller.listeners;

/**
 * Handles the event where an airport is created
 * It passes the airport parameters to the MasterModel
 */
public interface CreationListener<E> {
  void createParameter(E param) throws Exception;
}
