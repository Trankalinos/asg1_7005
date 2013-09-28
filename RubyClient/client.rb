# Assignment 1 - TCP/IP Socket Programming Client
# Version 1.2
# Author: Martin Javier
# Co-Author: David Tran
# Date: September 22nd, 2013
# Due Date: October 1st, 2013
require 'socket'
include Socket::Constants

# This class will receive a binary or a text file over a TCP/IP connection from the host.
class Client


  puts "Port"
  $stdin.flush
  port = gets.chomp
  puts "Server IP address"
  server_ip = gets.chomp

  socket = Socket.new(AF_INET, SOCK_STREAM, 0)
  sockaddr = Socket.sockaddr_in(port, server_ip)



  # Begins the connection to the server #{=AddrInfo=192.168.0.X:7005}
  begin
    socket.connect_nonblock(sockaddr)
  rescue IO::WaitWritable
    IO.select([socket]) # Wait 3-way handshake completion
    begin
      socket.connect_nonblock(sockaddr)
    rescue Errno::EISCONN
    end
  end


  socket.connect(sockaddr)

  puts "What would you like to do?"
  gets




  puts "The server said, '#{socket.readline.chomp}'"




  # Receive the binary or text file from the server.
  def getFile
    data = socket.read
    destinationFile = File.open('text.txt', 'wb')
    destinationFile.print data
    destinationFile.close
  end

  # Delivers the binary or text file to the server
  def sendFile
    file = open('./text.txt', 'rb')
    fileTransfer = file.read
    socket.puts(fileTransfer)
  end

  socket.close


end