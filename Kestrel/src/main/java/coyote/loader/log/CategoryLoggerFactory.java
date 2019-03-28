package coyote.loader.log;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * 
 */
public class CategoryLoggerFactory implements ILoggerFactory {

  private ConcurrentMap<String, Logger> loggerMap;




  public CategoryLoggerFactory() {
    loggerMap = new ConcurrentHashMap<String, Logger>();
  }




  /**
   * Return an appropriate {@link CategoryLogger} instance by name.
   */
  @Override
  public Logger getLogger( String name ) {
    Logger simpleLogger = loggerMap.get( name );
    if ( simpleLogger != null ) {
      return simpleLogger;
    } else {
      Logger newInstance = new CategoryLogger( name );
      Logger oldInstance = loggerMap.putIfAbsent( name, newInstance );
      return oldInstance == null ? newInstance : oldInstance;
    }
  }

}