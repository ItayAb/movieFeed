import java.sql.Date;
import java.sql.Time;

import javax.sql.rowset.spi.SyncResolver;


public class Logger {

	private static Logger m_logger = null;
	private static Object lock = new Object();
	public static Logger GetInstance(){
		if (m_logger == null) {
			synchronized (lock) {
				if (m_logger == null) {
					m_logger = new Logger();	
				}
				return m_logger;
			}
		} else{
			return m_logger;
		}
	}
	
	private Logger(){
		
	}
	
	public void Log(String format, Object ... args){
		synchronized (lock) {
			String message = String.format(format, args);
			System.out.printf("%s %s\n", Thread.currentThread().getStackTrace()[2], message);  			
		}
	}
	
	public void Log(String logMessage){
		synchronized (lock) {
			System.out.printf("%s %s\n", Thread.currentThread().getStackTrace()[2], logMessage);  			
		}
	}
}
