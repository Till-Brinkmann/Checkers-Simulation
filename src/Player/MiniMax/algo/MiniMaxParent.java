package algo;
/**
 * This interface only exists to simplify referencing the parent in MinMaxTasks.
 * The parent could be a MinMaxMTPlayer OR another MinMaxTask,
 * so we have to provide a uniform interface for this one method.
 * @author Till
 *
 */
public interface MiniMaxParent {
	/**
	 * It is not possible to declare interface methods protected
	 * and in this special case an abstract class would not really work
	 * so we have to declare this method public.
	 * @param child the task that is finished
	 */
	public void notifyFinished(MiniMaxMTTask child);
}
