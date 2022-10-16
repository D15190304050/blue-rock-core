# Conventions for development.

## Bucket Name
For each user with ID `id`, we set the bucket of the user as `bucket-of-#{id}`. This make it unnecessary to store the bucket information for users.

## Initialization for new users
Run the `initialize()` method for every new user.

## ID of directories
For each user, the root directory also has its own id (assigned when initializing).
And we set the `parent_id` of the root directories as 0.

Query to get the id of the root directory for a user:
```SQL
SELECT id FROM user_directory WHERE user_id = #{userId} AND parent_id = 0.
```