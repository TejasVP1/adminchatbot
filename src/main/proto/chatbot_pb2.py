# -*- coding: utf-8 -*-
# Generated by the protocol buffer compiler.  DO NOT EDIT!
# source: chatbot.proto
"""Generated protocol buffer code."""
from google.protobuf import descriptor as _descriptor
from google.protobuf import descriptor_pool as _descriptor_pool
from google.protobuf import symbol_database as _symbol_database
from google.protobuf.internal import builder as _builder
# @@protoc_insertion_point(imports)

_sym_db = _symbol_database.Default()




DESCRIPTOR = _descriptor_pool.Default().AddSerializedFile(b'\n\rchatbot.proto\x12\x07\x63hatbot\"\x1f\n\tUserQuery\x12\x12\n\nuser_input\x18\x01 \x01(\t\" \n\x0bSQLResponse\x12\x11\n\tsql_query\x18\x01 \x01(\t\"!\n\x0cQueryResults\x12\x11\n\tjson_data\x18\x01 \x01(\t\"+\n\x11\x46ormattedResponse\x12\x16\n\x0e\x66ormatted_text\x18\x01 \x01(\t2\x8d\x01\n\x0e\x43hatbotService\x12\x37\n\x0bGenerateSQL\x12\x12.chatbot.UserQuery\x1a\x14.chatbot.SQLResponse\x12\x42\n\rFormatResults\x12\x15.chatbot.QueryResults\x1a\x1a.chatbot.FormattedResponseb\x06proto3')

_globals = globals()
_builder.BuildMessageAndEnumDescriptors(DESCRIPTOR, _globals)
_builder.BuildTopDescriptorsAndMessages(DESCRIPTOR, 'chatbot_pb2', _globals)
if _descriptor._USE_C_DESCRIPTORS == False:

  DESCRIPTOR._options = None
  _globals['_USERQUERY']._serialized_start=26
  _globals['_USERQUERY']._serialized_end=57
  _globals['_SQLRESPONSE']._serialized_start=59
  _globals['_SQLRESPONSE']._serialized_end=91
  _globals['_QUERYRESULTS']._serialized_start=93
  _globals['_QUERYRESULTS']._serialized_end=126
  _globals['_FORMATTEDRESPONSE']._serialized_start=128
  _globals['_FORMATTEDRESPONSE']._serialized_end=171
  _globals['_CHATBOTSERVICE']._serialized_start=174
  _globals['_CHATBOTSERVICE']._serialized_end=315
# @@protoc_insertion_point(module_scope)
