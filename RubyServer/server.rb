# Assignment 1 - TCP/IP Socket Programming Server
# Author: Martin Javier
# Co-Author: David Tran
# Date: September 22nd, 2013
# Due Date: October 1st, 2013
require 'socket'


# This class will send a binary or a text file over a TCP/IP connection between two hosts.
class Server

  address = '127.0.0.1'
  port = 7005
  server = TCPServer.new(address, port)

  loop {

      Thread.start(server.accept) do |client|
      file = open('/Users/MartinMacPro/Desktop/test.txt', 'rb')
      blob = file.read
      client.puts(blob)
      client.close
      end
  }
end
