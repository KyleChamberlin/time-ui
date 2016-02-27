(ns hackathon.core
  (:require [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]
            [clojure.string :as string]))

(def app-state
  (atom
    {:app/title "Bravo Time"
     :time/entries [{:user "Dave" :project "Hackathon" :start "2016-02-27T14:00:00+05:00" :duration (* 8 60 60 1000)}
                    {:user "Kyle" :project "Hackathon" :start "2016-02-27T14:00:00+05:00" :duration (* 8 60 60 1000)}]
     :animals/list
     [[1 "Ant"] [2 "Antelope"] [3 "Bird"] [4 "Cat"] [5 "Dog"]
      [6 "Lion"] [7 "Mouse"] [8 "Monkey"] [9 "Snake"] [10 "Zebra"]]}))

(defmulti read (fn [env key params] key))

(defmethod read :default
  [{:keys [state] :as env} key params]
  (let [st @state]
    (if-let [[_ value] (find st key)]
      {:value value}
      {:value :not-found})))

(defmethod read :animals/list
  [{:keys [state] :as env} key {:keys [start end]}]
  {:value (subvec (:animals/list @state) start end)})

(defmulti mutate (fn [env key params] key))

(defmethod mutate :time/add-entry
  [{:keys [state] :as env} key {:keys [new-entry]}]
  {:value {:key [:time/entries]}
   :action #(swap! state update-in [:time/entries] (conj (:time/entries state) new-entry))})

(defui TimeEntryForm
  static om/IQuery
  (query [this]
         '[:app/title :time/entries])
  Object
  (render [this]
          (let [{:keys [app/title time/entries]} (om/props this)]
          (dom/div nil
                   (dom/h2 nil title)
                   (dom/form nil
                             (dom/label nil "User: " (dom/input #js {:type "text"}))
                             (dom/label nil "Project: " (dom/input #js {:type "text"}))
                             (dom/label nil "Start: " (dom/input #js {:type "text"}))
                             (dom/label nil "Duration: " (dom/input #js {:type "text"}))
                             (dom/button nil "Submit"))
                   (dom/h2 nil "Existing Entries")
                   (apply dom/ul nil
                          (map (fn [{:keys [user project start duration]}]
                                 (dom/li nil (string/join " " [user project start duration])))
                               entries))))))

(def reconciler
  (om/reconciler
    {:state app-state
     :parser (om/parser {:read read})}))

(om/add-root! reconciler
  TimeEntryForm (gdom/getElement "app"))
