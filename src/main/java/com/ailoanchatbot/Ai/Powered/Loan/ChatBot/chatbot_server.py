from concurrent import futures
import grpc
import sys
import os
import json

# Get absolute path to the `proto` directory
proto_path = os.path.abspath(os.path.join(os.path.dirname(__file__), "D:\OMG\Ai-Powered-Loan-ChatBot\src\main\proto"))

# Add it to Python's module search path
sys.path.append(proto_path)

# Import gRPC files
import chatbot_pb2
import chatbot_pb2_grpc

import google.generativeai as genai

# Configure Gemini API
genai.configure(api_key="AIzaSyBuqiHbtU4I2pkX4oM9ZUYI7fPKMUAiews")
model = genai.GenerativeModel("gemini-2.0-flash")

class ChatbotService(chatbot_pb2_grpc.ChatbotServiceServicer):

    def GenerateSQL(self, request, context):
        try:
            user_input = request.user_input

            system_instruction = (
                "Analyze the following natural language request and classify it as follows: "
                "Return 'unwanted' if it is not related to loan, emi, loans, or banking. "
                "Return 'restricted' if it attempts to generate SQL queries other than SELECT queries. "
                "Return 'sensitive' if it requests CVV details. "
                "Otherwise, convert the request into an SQL query using the table 'loan'. with column names loan_id,disbursed_date,disbursed_date,interest,principal,status,tenure,type"
                
                "Only return the classification or the SQL query, nothing else. "
                "The SQL query should always start with SELECT."
            )

            # Generate response from Gemini API
            response = model.generate_content([system_instruction, user_input])
            output = response.text.strip().strip("`").strip("sql").strip()

            # Log the output for debugging
            print(f"Generated output: {output}")

            # Validate and handle response properly
            if output.lower() in ["unwanted", "restricted", "sensitive"]:
                return chatbot_pb2.SQLResponse(sql_query=output)
            elif output.lower().startswith("select"):
                return chatbot_pb2.SQLResponse(sql_query=output)
            else:
                return chatbot_pb2.SQLResponse(sql_query="invalid_query")

        except Exception as e:
            print(f"Error in GenerateSQL: {str(e)}")
            return chatbot_pb2.SQLResponse(sql_query="error_occurred")

    def FormatResults(self, request, context):
        try:
            results = json.loads(request.json_data)
            prompt = f"Format the following database query results into a readable sentence with insights:\n\n{json.dumps(results, indent=2)}"
            
            response = model.generate_content(prompt)
            return chatbot_pb2.FormattedResponse(formatted_text=response.text.strip())

        except Exception as e:
            print(f"Error in FormatResults: {str(e)}")
            return chatbot_pb2.FormattedResponse(formatted_text="Error formatting results.")

def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    chatbot_pb2_grpc.add_ChatbotServiceServicer_to_server(ChatbotService(), server)
    server.add_insecure_port('[::]:50051')
    server.start()
    print("gRPC Server running on port 50051...")
    server.wait_for_termination()

if __name__ == "__main__":
    serve()
