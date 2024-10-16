#include "tcp_handler.h"

void TcpHandler::monitor(AMQP::TcpConnection *connection, int fd, int flags)
{
    // Implement your event loop integration here
}

void TcpHandler::onReady(AMQP::TcpConnection *connection)
{
    // Connection is ready, can send messages now
    sendMessage(connection);
}
