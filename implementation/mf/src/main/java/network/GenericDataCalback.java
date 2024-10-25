package network;

import messages.AbstractMessage;


/**
 * A generic callback class for defining simple callbacks in-line.
 *
 * @param <MessageType> Class type of the subscribed Message
 * @param <DataType> Class type of the callback member variable
 */
public abstract class GenericDataCalback<MessageType extends AbstractMessage, DataType> implements Callback<MessageType> {
	protected DataType data;

	public GenericDataCalback(DataType data) {
		super();
		this.data = data;
	}

	public DataType getData() {
		return this.data;
	}

}
