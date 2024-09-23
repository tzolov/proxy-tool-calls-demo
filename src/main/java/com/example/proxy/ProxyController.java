/*
* Copyright 2024 - 2024 the original author or authors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* https://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.example.proxy;

import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletion;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionChunk;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;

/**
 * @author Christian Tzolov
 * @since 1.0.0
 */
@RestController
public class ProxyController {

	private final ChatClient chatClient;

	ProxyController(ChatClient.Builder builder) {
		this.chatClient = builder.build();
	}

	@GetMapping("/ai/test")
	public Map<String, String> test(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
		return Map.of("completion", chatClient.prompt().user(message).call().content());
	}

	@PostMapping("/v1/chat/completions")
	public ChatCompletion completion(@RequestBody ChatCompletionRequest completionRequest) {

		Prompt prompt = OpenAiApiAdapter.toPrompt(completionRequest);

		ChatResponse chatResponse = chatClient.prompt(prompt).call().chatResponse();

		ChatCompletion chatCompletion = OpenAiApiAdapter.toChatCompletion(chatResponse);

		return chatCompletion;
	}

	@PostMapping(path = "/stream/v1/chat/completions", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<ChatCompletionChunk> streamCompletion(@RequestBody ChatCompletionRequest completionRequest) {

		return chatClient.prompt(OpenAiApiAdapter.toPrompt(completionRequest))
			.stream()
			.chatResponse()
			.map(OpenAiApiAdapter::toChatCompletionChunk);
	}
}
