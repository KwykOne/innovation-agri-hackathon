package handlers

import (
	"encoding/json"
	log "github.com/sirupsen/logrus"
	"github.com/vaibhavahuja/load-route-optimisation/internal/entities"
	"net/http"
)

//RouteOptimisationHandler This method is used to report any traffic/blocker/diversion on the road
//Our api relies on this api to update the weights of the graph
func (server *HttpServer) ReportRouteBlockerHandler(resp http.ResponseWriter, req *http.Request) {
	log.Info("received request")
	var request entities.ReportRouteBlocker
	decoder := json.NewDecoder(req.Body)
	err := decoder.Decode(&request)
	if err != nil {
		log.Errorf("Error while decoding request %s", err)
	}
	log.Info("sending output to request")
	response, err := server.svc.ReportRouteBlocker(request)
	if err != nil {
		log.Errorf("Erorr while calculating optimal route : %s", err)
		resp.WriteHeader(http.StatusInternalServerError)
	}

	resp.Header().Set("Content-Type", "application/json")
	resp.WriteHeader(http.StatusOK)
	jsonResponse, err := json.Marshal(response)
	if err != nil {
		return
	}
	_, err = resp.Write(jsonResponse)
	if err != nil {
		log.Errorf("Unable to write error %s", err)
	}
	log.Info("Successfully published record to API")
}
