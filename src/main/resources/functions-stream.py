import os
from openai import OpenAI
import json

# Initialize the OpenAI client
# client = OpenAI(api_key=os.getenv("OPENAI_API_KEY"))
client = OpenAI(api_key=os.getenv("OPENAI_API_KEY"), base_url="http://localhost:8080/stream/v1")

def get_weather(location):
    # This is a mock function. In a real app, you'd call a weather API here.
    return f"The weather in {location} is sunny and 72°F (22°C)."

def run_conversation(user_input):
    messages = [{"role": "user", "content": user_input}]
    tools = [
        {
            "type": "function",
            "function": {
                "name": "get_weather",
                "description": "Get the current weather in a given location",
                "parameters": {
                    "type": "object",
                    "properties": {
                        "location": {
                            "type": "string",
                            "description": "The city and state, e.g. San Francisco, CA",
                        },
                    },
                    "required": ["location"],
                },
            },
        }
    ]

    stream = client.chat.completions.create(
        model="gpt-4o-mini",
        messages=messages,
        tools=tools,
        tool_choice="auto",
        stream=True,
    )

    response_content = ""
    tool_calls = []

    for chunk in stream:
        print("############")
        print(chunk)
        print("############")
        if chunk.choices[0].delta.content is not None:
            response_content += chunk.choices[0].delta.content
        
        if chunk.choices[0].delta.tool_calls:
            for tool_call in chunk.choices[0].delta.tool_calls:
                if len(tool_calls) <= tool_call.index:
                    tool_calls.append({"id": tool_call.id, "type": "function", "function": {"name": "", "arguments": ""}})
                
                if tool_call.function.name:
                    tool_calls[tool_call.index]["function"]["name"] = tool_call.function.name
                
                if tool_call.function.arguments:
                    tool_calls[tool_call.index]["function"]["arguments"] += tool_call.function.arguments

    if tool_calls:
        available_functions = {
            "get_weather": get_weather,
        }
        messages.append({"role": "assistant", "content": response_content, "tool_calls": tool_calls})

        for tool_call in tool_calls:
            function_name = tool_call["function"]["name"]
            function_to_call = available_functions[function_name]
            function_args = json.loads(tool_call["function"]["arguments"])
            function_response = function_to_call(
                location=function_args.get("location")
            )
            messages.append(
                {
                    "tool_call_id": tool_call["id"],
                    "role": "tool",
                    "name": function_name,
                    "content": function_response,
                }
            )

        second_stream = client.chat.completions.create(
            model="gpt-4o-mini",
            messages=messages,
            stream=True,
        )

        final_response = ""
        for chunk in second_stream:
            if chunk.choices[0].delta.content is not None:
                final_response += chunk.choices[0].delta.content
        
        return final_response
    
    return response_content

# Example usage
user_input = "What's the weather like in New York?"
result = run_conversation(user_input)
print(result)