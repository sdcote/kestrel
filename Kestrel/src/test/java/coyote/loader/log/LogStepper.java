package coyote.loader.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


/**
 * 
 */
public class LogStepper {




  /**
   *
   */
  public static void stepOne() {

    // SLF4J
    Logger LOG = LoggerFactory.getLogger( LogStepper.class );
    LOG.trace( "trace" );
    LOG.debug( "debug" );
    LOG.info( "info" );
    LOG.warn( "warn" );
    LOG.error( "error" );
    System.out.println( "============================" );
    Log.startLogging( Log.TRACE );

    LOG.trace( "trace" );
    LOG.debug( "debug" );
    LOG.info( "info" );
    LOG.warn( "warn" );
    LOG.error( "error" );

  }




  public static void stepTwo() {

    // Add a logger that will send log messages to the console 
    Log.addLogger( "Loader", new ConsoleAppender( Log.INFO_EVENTS | Log.WARN_EVENTS | Log.ERROR_EVENTS | Log.FATAL_EVENTS ) );

    FileAppender appender = new FileAppender( new File( "debug.log" ) );
    appender.startLogging( Log.DEBUG );
    Log.addLogger( "Debugfile", appender );

    //Log.startLogging( Log.DEBUG );
    //Log.startLogging( Log.TRACE );

    Log.trace( "trace" );
    Log.debug( "debug" );
    Log.info( "info" );
    Log.warn( "warn" );
    Log.error( "error" );
    System.out.println( "============================" );
    Log.startLogging( Log.TRACE );

    Log.trace( "trace" );
    Log.debug( "debug" );
    Log.info( "info" );
    Log.warn( "warn" );
    Log.error( "error" );
  }




  /**
   * @param args
   */
  public static void main( String[] args ) {

  }
}
