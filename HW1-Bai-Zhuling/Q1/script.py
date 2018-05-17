import tweepy
import json
import csv
import time


def loadKeys(key_file):
    # TODO: put your keys and tokens in the keys.json file,
    #       then implement this method for loading access keys and token from keys.json
    # rtype: str <api_key>, str <api_secret>, str <token>, str <token_secret>
    # Load keys here and replace the empty strings in the return statement with those keys
    with open(key_file,'r') as f:
        key = json.load(f);
    return key['api_key'],key['api_secret'],key['token'],key['token_secret']

# Q1.b.(i) - 5 points
def getPrimaryFriends(api, root_user, no_of_friends):
    # TODO: implement the method for fetching 'no_of_friends' primary friends of 'root_user'
    # rtype: list containing entries in the form of a tuple (root_user, friend)
    user = api.get_user(root_user)
    primary_friends = []
    for friend in tweepy.Cursor(api.friends,id=user.id).items(no_of_friends):
        primary_friends.append((user.screen_name,friend.screen_name))                        
    # Add code here to populate primary_friends
    return primary_friends

# Q1.b.(ii) - 7 points
def getNextLevelFriends(api, friends_list, no_of_friends):
    # TODO: implement the method for fetching 'no_of_friends' friends for each entry in friends_list
    # rtype: list containing entries in the form of a tuple (friends_list[i], friend)
    next_level_friends = []
    for i in xrange(len(friends_list)):
        user = api.get_user(friends_list[i])
        time.sleep(60)
        for friend in tweepy.Cursor(api.friends,id=user.id).items(no_of_friends):
            next_level_friends.append((user.screen_name, friend.screen_name))  
                         
    # Add code here to populate next_level_friends
    return next_level_friends

# Q1.b.(iii) - 7 points
def getNextLevelFollowers(api, followers_list, no_of_followers):
    # TODO: implement the method for fetching 'no_of_followers' followers for each entry in followers_list
    # rtype: list containing entries in the form of a tuple (follower, followers_list[i])    
    next_level_followers = []
    for i in xrange(len(followers_list)):
        user = api.get_user(followers_list[i])
        time.sleep(60)
        for follower in tweepy.Cursor(api.followers,id=user.id).items(no_of_followers):
            next_level_followers.append((follower.screen_name, user.screen_name))
    # Add code here to populate next_level_followers
    return next_level_followers

# Q1.b.(i),(ii),(iii) - 4 points
def GatherAllEdges(api, root_user, no_of_neighbours):
    # TODO:  implement this method for calling the methods getPrimaryFriends, getNextLevelFriends
    #        and getNextLevelFollowers. Use no_of_neighbours to specify the no_of_friends/no_of_followers parameter.
    #        NOT using the no_of_neighbours parameter may cause the autograder to FAIL.
    #        Accumulate the return values from all these methods.
    # rtype: list containing entries in the form of a tuple (Source, Target). Refer to the "Note(s)" in the 
    #        Question doc to know what Source node and Target node of an edge is in the case of Followers and Friends.    
    user = api.get_user(root_user)
    friends_list = []
    for friend in user.friends():
        friends_list.append(friend.screen_name)
    all_edges = [] 
    primary_friends = getPrimaryFriends(api, root_user, no_of_neighbours)
    next_level_friends = getNextLevelFriends(api, friends_list, no_of_neighbours)
    next_level_followers = getNextLevelFollowers(api, friends_list, no_of_neighbours)
    all_edges.append(primary_friends)
    all_edges.append(next_level_friends)
    all_edges.append(next_level_followers)
    #Add code here to populate all_edges
    return all_edges

# Q1.b.(i),(ii),(iii) - 5 Marks
def writeToFile(data, output_file):
    # write data to output_file
    # rtype: None
    with open(output_file,'wb') as f:
        writer = csv.writer(f)
        for e in data:
            writer.writerows(e)
    pass

def testSubmission():
    KEY_FILE = 'keys.json'
    OUTPUT_FILE_GRAPH = 'graph1.csv'
    NO_OF_NEIGHBOURS = 20
    ROOT_USER = 'PoloChau'

    api_key, api_secret, token, token_secret = loadKeys(KEY_FILE)

    auth = tweepy.OAuthHandler(api_key, api_secret)
    auth.set_access_token(token, token_secret)
    api = tweepy.API(auth)

    edges = GatherAllEdges(api, ROOT_USER, NO_OF_NEIGHBOURS)

    writeToFile(edges, OUTPUT_FILE_GRAPH)
    

if __name__ == '__main__':
    testSubmission()
