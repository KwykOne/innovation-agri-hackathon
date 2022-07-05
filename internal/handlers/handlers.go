package handlers

import (
	"github.com/vaibhavahuja/load-route-optimisation/internal/service"
)

type HttpServer struct {
	svc *service.Application
}

func NewHttpServer(svc *service.Application) HttpServer {
	return HttpServer{svc: svc}
}
