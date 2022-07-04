package handlers

import (
	"fmt"
	"net/http"
)

func (server *HttpServer) HealthCheck(resp http.ResponseWriter, req *http.Request) {
	resp.WriteHeader(http.StatusOK)
	fmt.Fprintf(resp, "Working fine")
}
