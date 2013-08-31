package org.yuner.www.client;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import org.yuner.www.commons.*;
import org.yuner.www.beans.SendStackItem;

public class ClientSendThread extends Thread {

	private BufferedWriter mBuffWter;
	private ArrayList<SendStackItem> mSendList;
	
	private boolean mIsRunning;
	private boolean mReceived;    // the heartbeat message from client verifying they have received the message you send

	public ClientSendThread(BufferedWriter bufw) {
		mBuffWter = bufw;
		mSendList = new ArrayList<SendStackItem>();
	}

	public void run() {

		super.run();
		mIsRunning = true;

		while(mIsRunning) {
			if(mSendList.size() == 0) {
				try {
					sleep(60);
				} catch(Exception eer) { eer.printStackTrace(); }
			} else {
				SendStackItem item0 = mSendList.get(0);   // get one at the head

				if(item0 != null) {
					send(item0.getType() + "");
					send(item0.getStr());
				} else {
					System.out.println("error, item0 = " + item0);
					continue;
				}

				mReceived = false;
				while(!mReceived) {
					try {
						sleep(3);
					} catch(Exception e) { e.printStackTrace(); }
				}

				mSendList.remove(0);
			}
		}

	}

	public void close() {
		mIsRunning = false;
	}

	public synchronized void insert(int type, String str0) {
		switch(type) {
			case GlobalMsgTypes.msgFromFriend:
			case GlobalMsgTypes.msgFriendshipRequest:
			case GlobalMsgTypes.msgFriendshipRequestResponse:
				break;
			default:
				send(type + "");
				send(str0);
				return;
		}

		SendStackItem item0 = new SendStackItem(type, str0);
		mSendList.add(item0);    // add this item to the end of stack
	}

	private synchronized void send(String str0)
	{
		String strToSend = str0;
		
		strToSend = strToSend.replace("\n",GlobalStrings.replaceOfReturn);
		try {
			mBuffWter.write(strToSend + "\n");
			mBuffWter.flush();
			System.out.println(strToSend);
		} catch (Exception e) {}
	}

	public void setIsReceived() {
		mReceived = true;
	}
}
