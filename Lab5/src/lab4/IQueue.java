package lab4;

public interface IQueue<T> {
		void addElement(T data) throws InterruptedException;
		T removeElement() throws InterruptedException;
		int getElems();
		Boolean getActive();
		void setActive(Boolean active);
}
