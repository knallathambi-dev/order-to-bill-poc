const replicaSetName = process.env.MONGODB_REPLICA_SET || "rs0";

let initialized = false;

try {
  initialized = rs.status().ok === 1;
} catch (error) {
  initialized = false;
}

if (initialized) {
  print(`MongoDB replica set ${replicaSetName} already initialized`);
} else {
  rs.initiate({
    _id: replicaSetName,
    members: [
      {
        _id: 0,
        host: "mongodb:27017"
      }
    ]
  });
  print(`MongoDB replica set ${replicaSetName} initialization requested`);
}
