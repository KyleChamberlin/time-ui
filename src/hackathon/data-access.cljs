(ns hackathon.data-access
  (:require [clojure.string :as string]
			[matchbox.core :as m]))

(def ^:private base-uri "https://crackling-heat-3499.firebaseio.com/")

(def ^:private secret "your-api-key-here")

(defn ^:private firebase-connect
	"wrap firebase processing with connect/disconnect"
	[execute]
	{:pre [(fn? execute)]}
	(def ^:private connection (m/connect base-uri))
	(m/auth-custom connection secret)
	(execute connection)
	(m/unauth connection))

(defn time-entry-create
	"create a new time entry in the data store"
	[time-entry]
	{:pre [(not-nil? time-entry)]}
	(firebase-connect (fn [auth-connection] (m/conj! auth-connection time-entry))))
	
(defn time-entry-update
	"update a time entry already in the data store"
	[time-entry]
	{:pre [(not-nil? time-entry)]}
	(firebase-connect (fn [auth-connection] (m/merge! auth-connection time-entry))))

(defn time-entry-get
	"retrieve a time entry by id"
	[entryId]
	{:pre [(not-nil? entryId)]}
	(firebase-connect (fn [auth-connection] (m/get-in auth-connection ["timeEntry" entryId]))))

(defn time-entry-get-all
	"retrieve all time entities for a user"
	[^string userId]
	{:pre [(not-nil? userId)]}
	(firebase-connect (fn [auth-connection] (m/get-in auth-connection ["timeEntry" userId])))
	
