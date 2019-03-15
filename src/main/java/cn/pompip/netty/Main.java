package cn.pompip.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.DecoderResultProvider;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import kotlin.text.Charsets;

import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        try {
            new ServerBootstrap()
                    .group(new NioEventLoopGroup(), new NioEventLoopGroup())
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new MyChannel())
                    .bind(8000).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static class MyChannel extends ChannelInitializer<NioSocketChannel> {

        @Override
        protected void initChannel(NioSocketChannel ch) throws Exception {
//            ch.pipeline().addLast(new HttpServerCodec())
//                    .addLast(new LoggingHandler(LogLevel.INFO))

            ch.pipeline()
                    .addLast(new ChunkedWriteHandler())
                            .addLast("decoder", new HttpRequestDecoder())
                    .addLast("encoder", new HttpResponseEncoder())
                    .addLast(new HttpObjectAggregator(8192))
                    .addLast(new WebSocketServerProtocolHandler("/ws"))

//                    .addLast("httpServerCodec", new HttpServerCodec())
                    .addLast(new SimpleChannelInboundHandler<Object>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
//                            System.out.println(msg.uri());
//                            System.out.println(msg.method());
                            System.out.println(msg.getClass());
                            DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                                    HttpResponseStatus.OK, Unpooled.wrappedBuffer("httl".getBytes()));

                            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=UTF-8");
                            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
                            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);

                        }

                        @Override
                        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                            super.channelReadComplete(ctx);
                            System.out.println("channelReadComplete");

                        }
                    })
                    .addLast(new SimpleChannelInboundHandler<TextWebSocketFrame>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
                            System.out.println(msg.text());
                            ctx.channel().writeAndFlush(new TextWebSocketFrame("服务器时间: " + LocalDateTime.now()));
                        }


                    });
        }
    }
}
