package MOM;

public class MessageObject {
	private String text;
	private String sender;
	private String receiver;

	public MessageObject(String sender, String receiver, String text) {
		this.sender = sender;
		this.receiver = receiver;
		this.text = text;
	}

	public String getText() {
		return this.text;
	}

	public String getSender() {
		return this.sender;
	}

	public String getReceiver() {
		return this.receiver;
	}
}
