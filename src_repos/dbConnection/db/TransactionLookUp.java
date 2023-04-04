package twx.core.db;

import ch.qos.logback.classic.Logger;
import com.thingworx.logging.LogUtilities;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TransactionLookUp {
  private static Logger _logger = LogUtilities.getInstance().getApplicationLogger(TransactionLookUp.class);
  
  private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
  
  private ScheduledFuture<?> transactionCleanerHandle;
  
  private TransactionManager transactionManager;
  
  public TransactionLookUp(TransactionManager transactionManager) {
    this.transactionManager = transactionManager;
  }
  
  void start() {
    Runnable transactionCleaner = () -> {
        try {
          Map<Object, Transaction> transactionMap = this.transactionManager.getTransactionMap();
          _logger.warn("Look to clean transactions:" + transactionMap.size());
          if (transactionMap.size() > 0) {
            System.gc();
            for (Map.Entry<Object, Transaction> entry : transactionMap.entrySet()) {
              _logger.warn("Checking transaction:" + entry.getKey());
              Transaction transaction = entry.getValue();
              transaction.setDepth(0);
              try {
                _logger.warn("Rollback transaction:" + entry.getKey());
              } catch (Exception ex) {
                _logger.warn("Error rolling back transaction for terminated thread", ex);
              } finally {
                _logger.warn("Closing transaction:" + entry.getKey());
              } 
            } 
          } 
        } catch (Exception e) {
          _logger.error("Error in transaction look up", e);
        } 
      };
    this
      .transactionCleanerHandle = this.scheduledExecutorService.scheduleAtFixedRate(transactionCleaner, 30L, 30L, TimeUnit.SECONDS);
  }
  
  void stop() {
    if (this.transactionCleanerHandle != null)
      this.scheduledExecutorService.schedule(() -> this.transactionCleanerHandle.cancel(true), 0L, TimeUnit.SECONDS); 
  }
}
