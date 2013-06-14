package BiNGO.BiNGO;

public abstract interface MonitorableTask
{
  public abstract boolean isDone();

  public abstract int getCurrentProgress();

  public abstract int getLengthOfTask();

  public abstract String getTaskDescription();

  public abstract String getCurrentStatusMessage();

  public abstract void start(boolean paramBoolean);

  public abstract void stop();

  public abstract boolean wasCanceled();
}