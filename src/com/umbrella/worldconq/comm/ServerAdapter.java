package com.umbrella.worldconq.comm;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.UUID;

import com.umbrella.worldconq.domain.Session;
import communications.IServer;

import domain.Game;
import domain.GameInfo;

public class ServerAdapter {

	private String mRemoteName;
	private InetAddress mRemoteHost;
	private int mRemotePort;
	private IServer mProxy;

	public ServerAdapter() {
		mRemoteName = null;
		mRemoteHost = null;
		mRemotePort = 0;
		mProxy = null;
	}

	public ServerAdapter(String remoteName, InetAddress remoteHost, int remotePort) {
		this.setRemoteInfo(remoteName, remoteHost, remotePort);
		mProxy = null;
	}

	public void setRemoteInfo(String remoteName, InetAddress remoteHost, int remotePort) {
		mRemoteName = remoteName;
		mRemoteHost = remoteHost;
		mRemotePort = remotePort;
	}

	public void connect() throws MalformedURLException, RemoteException, NotBoundException {
		this.disconnect();
		final String url = String.format("rmi://%s:%d/%s",
			mRemoteHost.getHostAddress(),
			mRemotePort,
			mRemoteName);
		mProxy = (IServer) Naming.lookup(url);
	}

	public void disconnect() {
		mProxy = null;
	}

	public boolean isConnected() {
		return mProxy != null;
	}

	public UUID createSession(String login, String passwd) throws Exception {
		if (!this.isConnected()) throw new RemoteException();
		// TODO Falta el callback
		return mProxy.loginUser(login, passwd, null);
	}

	public void closeSession(Session session) throws Exception {
		if (!this.isConnected()) throw new RemoteException();
		mProxy.logoutUser(session.getId());
	}

	public void registerUser(String login, String passwd, String email) throws Exception {
		if (!this.isConnected()) throw new RemoteException();
		mProxy.registerUser(login, passwd, email);
	}

	public ArrayList<GameInfo> fetchGameList(Session session) throws Exception {
		if (!this.isConnected()) throw new RemoteException();
		return mProxy.listGames(session.getId());
	}

	public void createGame(Session session, GameInfo game) throws Exception {
		if (!this.isConnected()) throw new RemoteException();
		mProxy.createGame(session.getId(), game);
	}

	public void joinGame(Session session, GameInfo game) throws Exception {
		if (!this.isConnected()) throw new RemoteException();
		mProxy.joinGame(session.getId(), game.getId());
	}

	public Game playGame(Session session, GameInfo game) throws Exception {
		if (!this.isConnected()) throw new RemoteException();
		return mProxy.playGame(session.getId(), game.getId());

	}

}