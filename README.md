# Whitetail

Whitetail is a P2P proxy software aiming to resolve the common
issues with privacy-friendly alternative frontends/backends for
not-so-privacy friendly services like YouTube, TikTok, Twitter/X or Reddit.

## Background

After the Reddit API changes effectively rendering Teddit/Libreddit unusable,
as well as X's pushback against Nitter, as well as in the light of issues with Invidious/Piped
instances related to their performance and YouTube limiting the available features like
closed captions once 'too much' requests are made from a single host, I've decided that
something should be done about it.

The current typical architecture for such proxies is based on the client-server model,
where the financial burden of operating such proxy could become quite significant.

This, combined with low turnover of available instances makes them a relatively easy target for
blockage by their respective targets.

## How it works

By offloading the resource transmission across multiple nodes, we are able to make it much harder
to rate limit privacy-friendly proxies, while improving their performance and 
by obfuscating the incoming connections, also strenthen the trust and protection against abuse by malicious actors.

On the data provider side, we also implement protections against tampering by creating a trust
score system and selectively verifying that parts of the sent data are identical for the same
resource if requested from other nodes.

When you start whitetail, you need to provide at least one service's name in the configuration file, alongside with the port it's listening on.

Then, Whitetail will make some requests to the service to check if it provides the expected data.
In the future we hope to see the services encapsulated by Whitetail provide a specialized API 
for this purpose.

Whitetail will then expose these services to the Whitetail network.

As an example, let's assume Alice operates an Invidious instance. Bob then makes a request on 
his machine to `https://localhost:4501/watch?v=dQw4w9WgXcQ`, where 4501 is an outgoing port
for requesting resources from Invidious. Whitetail will then poll it's database for
nodes where an Invidious instance is running. It finds Alice's node, which has an ULID
`01HT8PVA2BZ2D0JZ1Z2MD8NJ2Z01HT8PVA2BZ2D0JZ1Z2MD8NJ2Z`. If Alice's node has the best ratio of load to latency relative to Bob, Whitetail will select it as the provider of the resource at `watch?v=dQw4w9WgXcQ`. But before sending a response to Bob, it will also check if the content at Alice's node is the same as at two randomly selected other nodes. This will not be done for every request in 
order to not degrade the performance, but will be done more often if Alice is a new peer in the 
network.
