package com.helloworldapi.service;

public class HelloWorldServiceImpl implements HelloWorldService {
	public HelloWorldServiceImpl() {
	}

	@Override
	public String postHelloWorld() {
		return "Hello, World!";
	}

	@Override
	public String getHelloWorld() {
		return "Hello, World!";
	}
}
