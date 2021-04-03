package com.softgames.popat.service;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class FireStoreService {

    private FirebaseFirestore fireStore;

    public FireStoreService() {
        fireStore = FirebaseFirestore.getInstance();
        //show toast something went wrong
    }

    public Task<DocumentSnapshot> getData(String collection, String document){

        CollectionReference collectionReference = fireStore.collection(collection);
        DocumentReference documentReference = collectionReference.document(document);

        return documentReference.get();
    }

    public Task<DocumentSnapshot> getDataSubCollection(String collection, String document, String subCollection, String subDocument){

        CollectionReference collectionReference = fireStore.collection(collection);
        DocumentReference documentReference = collectionReference.document(document);

        CollectionReference subCollectionReference = documentReference.collection(subCollection);
        DocumentReference subDocumentReference = subCollectionReference.document(subDocument);

        return subDocumentReference.get();
    }

    public Query getSubCollectionRef(String collection, String document, String subCollection){

        CollectionReference collectionReference = fireStore.collection(collection);
        DocumentReference documentReference = collectionReference.document(document);

        return documentReference.collection(subCollection);
    }



    public DocumentReference getSubCollectionSubDocumentRef(String collection, String document, String subCollection, String subDocument){

        CollectionReference collectionReference = fireStore.collection(collection);
        DocumentReference documentReference = collectionReference.document(document);

        CollectionReference collectionReference2 = documentReference.collection(subCollection);

        return collectionReference.document(subDocument);
    }

    public CollectionReference getSubCollection(String collection, String document, String subCollection){

        CollectionReference collectionReference = fireStore.collection(collection);
        DocumentReference documentReference = collectionReference.document(document);

        return documentReference.collection(subCollection);
    }

    public Task<Void> deleteSubCollectionSubDocument(String collection, String document, String subCollection, String subDoc){

        CollectionReference collectionReference = fireStore.collection(collection);
        DocumentReference documentReference = collectionReference.document(document);

        CollectionReference subCollectionReference = documentReference.collection(subCollection);
        DocumentReference subDocumentReference = subCollectionReference.document(subDoc);

        return subDocumentReference.delete();
    }

    public Task<Void> setData(String collection, String document, Object object){

        CollectionReference collectionReference = fireStore.collection(collection);
        DocumentReference documentReference = collectionReference.document(document);

        return documentReference.set(object);
    }

    public Task<Void> setDataSubCollection(String collection, String document, String subCollection, String subDocument, Object object){

        CollectionReference collectionReference = fireStore.collection(collection);
        DocumentReference documentReference = collectionReference.document(document);

        CollectionReference subCollectionReference = documentReference.collection(subCollection);
        DocumentReference subDocumentReference = subCollectionReference.document(subDocument);

        return subDocumentReference.set(object);
    }

    public Task<Void> deleteData(String collection, String document){

        CollectionReference collectionReference = fireStore.collection(collection);
        DocumentReference documentReference = collectionReference.document(document);

        return documentReference.delete();
    }

    public Task<Void> updateData(String collection, String document, Map<String, Object> map){

        CollectionReference collectionReference = fireStore.collection(collection);
        DocumentReference documentReference = collectionReference.document(document);

        return documentReference.update(map);
    }

    public Task<Void> updateData(DocumentReference documentReference, Map<String, Object> map){
        return documentReference.update(map);
    }

    public Task<QuerySnapshot> getWhereIn(String collection, String field, List<String> list){
        return fireStore.collection(collection).whereIn(field, list).get();

    }

    public Task<QuerySnapshot> getWhichContains(String collection, String field, Object object){

        return fireStore.collection(collection).whereArrayContains(field, object).get();
    }

    public Task<QuerySnapshot> getWhichEquals(String collection, String field, Object object){

        return fireStore.collection(collection).whereEqualTo(field, object).get();
    }
    public Task<QuerySnapshot> getQueryData(String collection, Map<String, Object> queryList, String orderBy, long limit){

        CollectionReference collectionReference = fireStore.collection(collection);

        Set<String> keys = queryList.keySet();
        Query query = null;

        for (String key : keys ) {
            if(query == null){
                query = collectionReference.whereEqualTo(key, queryList.get(key));
            }else {
                query = query.whereEqualTo(key, queryList.get(key));
            }

        }
        query.orderBy(orderBy);
        query.limit(limit);
        return query.get();

    }
    public DocumentReference getDocReference(String collection, String document){
        return fireStore.collection(collection).document(document);

    }
    public Task<QuerySnapshot> getRecentUpdates(String collection, String orderByField, int limit){
        return fireStore.collection(collection)
                .orderBy(orderByField, Query.Direction.DESCENDING)
                .limit(limit)
                .get();
    }

    public Task<String> getToken(){
        return FirebaseMessaging.getInstance().getToken();

    }

}
