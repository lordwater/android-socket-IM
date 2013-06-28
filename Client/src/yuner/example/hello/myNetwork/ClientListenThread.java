package yuner.example.hello.myNetwork;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

import yuner.example.hello.chatServices.FriendListInfo;
import yuner.example.hello.chatServices.InitData;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class ClientListenThread extends Thread{
	private Context mContext0;
	private Socket mSocket0;
	
	private InputStreamReader mInStrRder0;
	private BufferedReader mBuffRder0;
	
	public ClientListenThread(Context par,Socket s,NetConnect netCon)
	{
		this.mContext0=par;
		this.mSocket0=s;
	}

	public void run()
	{
		super.run();
		try{
			mInStrRder0=new InputStreamReader(mSocket0.getInputStream());
			mBuffRder0=new BufferedReader(mInStrRder0);
			String resp=null;
			
			while (true) {
				if ((resp = mBuffRder0.readLine()) != null) {
					String[] sbArr0 = resp.split(ChatEntity.strSplitter);
					int msgType = Integer.parseInt(sbArr0[0]);			// type of message received
					
					boolean readRealMsg = true;
					String actualMsg = "";
					while (readRealMsg) {
						if((resp = mBuffRder0.readLine()) != null) {
							actualMsg+=resp;
							if (!resp.endsWith(ChatEntity.strSplitter)) {
								actualMsg += "\n";
								continue;
							}
							
							switch (msgType) {
							case 0:
								/* falls through */
							case 1:
								/* falls through */
							case 2:	
								/* try here to secure for the possible message with first input character as 
								   "\n"	 */
								try {
									ChatEntity entTemp = ChatEntity.Str2Ent(actualMsg);
									Intent intent = new Intent("yuner.example.hello.MESSAGE_RECEIVED");
									intent.putExtra("yuner.example.hello.msg_received", entTemp.toString());
									intent.putExtra("yuner.example.hello.msg_type", msgType);
									mContext0.sendBroadcast(intent);
								} catch(Exception e) {
									actualMsg += "\n";
									continue;
								}
								break;
							case 3:
								try {
									UserInfo usrInfo = new UserInfo(actualMsg);								
									InitData.getInitData().msg3Arrive(usrInfo.toString());
								} catch(Exception e) { e.printStackTrace(); }
								break;
							case 5:
								try {
									InitData.getInitData().msg5Arrive(actualMsg);
								} catch(Exception e) { e.printStackTrace(); }
								break;
							case 6:		
								Intent intent = new Intent("yuner.example.hello.MESSAGE_RECEIVED");
								intent.putExtra("yuner.example.hello.msg_received", actualMsg);
								intent.putExtra("yuner.example.hello.msg_type", msgType);
								mContext0.sendBroadcast(intent);
							//	try {
							//		FriendListInfo.getFriendListInfo().updateFriendList(actualMsg);
							//	} catch(Exception e) { e.printStackTrace(); }
								break;
							default:
								break;
							}
							readRealMsg = false;
						}
					}
				}
			}
		}catch(Exception e){}
	}
	
	public void closeBufferedReader()
	{
		try {
			mBuffRder0.close();
		} catch(Exception e) {}
	}
}
