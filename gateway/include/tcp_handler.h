#ifndef MYTCPHANDLER_H
#define MYTCPHANDLER_H

#include <amqpcpp.h>
#include <amqpcpp/linux_tcp.h>
#include <iostream>

class TcpHandler : public AMQP::TcpHandler
{
public:
    void monitor(AMQP::TcpConnection *connection, int fd, int flags) override;

    void onReady(AMQP::TcpConnection *connection) override;
};

#endif //MYTCPHANDLER_H
