package com.turin.tur.main.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.net.HttpStatus;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.turin.tur.main.diseno.Enviables;



public class Internet {

	private static final String TAG = Internet.class.getName();
	public static boolean internetChecked=false;
	public static boolean serverOnline=false;
	public static String serverStatus="";
	public static HttpStatus serverStatusCode;
	public static boolean serverOk;
	public static final String server = "http://turintur.dynu.com/";
	
	public static void Check() {
		serverOnline = false; // Reinicia el status del server
		serverOk = false;
		internetChecked=true; // Indica que comenzo a chaequear
		new Thread(new Runnable() {

			@Override
			public void run() {

				String requestJson = "";
				
				final Net.HttpRequest request = new Net.HttpRequest(HttpMethods.GET);
				request.setContent(requestJson);

				request.setHeader("Content-Type", "application/json");
				request.setHeader("Accept", "application/json");
				request.setUrl(server+"status/");

				Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {

					public void handleHttpResponse(Net.HttpResponse httpResponse) {

						serverOnline=true;
						serverStatus = httpResponse.getStatus().toString();
						serverStatusCode= httpResponse.getStatus();
						String rta = httpResponse.getResultAsString();
						if (rta.contains("on")) {
							serverOk=true;
							System.out.println("Server ok");
						} else {
							serverOk = false;
							System.out.println("Server not ok");
						}
						
					}

					public void failed(Throwable t) {
						serverOnline=false;
						System.out.println("Request Failed Completely");
					}

					@Override
					public void cancelled() {
						serverOnline=false;
						System.out.println("request cancelled");
					}

				});

			}
		}).start();
		
	}
	
	public static void PUT(final Enviables objetoEnviado) {

		Array<String> urls = new Array<String>();
		urls.add("http://turintur.dynu.com/" + objetoEnviado.getClass().getSimpleName());
		//urls.add("http://181.169.225.117:3000/" + objetoEnviado.getClass().getSimpleName());

		for (final String url : urls) {

			new Thread(new Runnable() {

				@Override
				public void run() {

					Json json = new Json();
					json.setOutputType(OutputType.json);
					String requestJson = json.toJson(objetoEnviado.contenido);

					Net.HttpRequest request = new Net.HttpRequest(HttpMethods.POST);
					request.setContent(requestJson);

					request.setHeader("Content-Type", "application/json");
					request.setHeader("Accept", "application/json");
					request.setUrl(url);

					System.out.println(url);
					Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {

						public void handleHttpResponse(Net.HttpResponse httpResponse) {

							int statusCode = httpResponse.getStatus().getStatusCode();
							System.out.println(url);
							System.out.println(httpResponse.getStatus().getStatusCode());
							if (statusCode != HttpStatus.SC_CREATED) {
								Gdx.app.debug(TAG, "" + httpResponse.getStatus().getStatusCode());
								objetoEnviado.noEnviado();
								System.out.println("Request Failed");
							} else {
								objetoEnviado.enviado();
							}
						}

						public void failed(Throwable t) {
							System.out.println(t);
							objetoEnviado.noEnviado();
							System.out.println("Request Failed Completely");
						}

						@Override
						public void cancelled() {
							objetoEnviado.noEnviado();
							System.out.println("request cancelled");
						}

					});

				}
			}).start();
		}
	}
		
}
