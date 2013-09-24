# Assignment 1 - TCP/IP Socket Programming Client
# Author: Martin Javier
# Co-Author: David Tran
# Date: September 22nd, 2013
# Due Date: October 1st, 2013
require 'socket'

# This class will receive a binary or a text file over a TCP/IP connection from the host.
class Client

  socket = TCPSocket.open('127.0.0.1', 7005)
  data = socket.read

  destFile = File.open('text.txt', 'wb')
  destFile.print data
  destFile.close

end