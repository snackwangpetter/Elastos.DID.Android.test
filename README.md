# Elastos.DID.Android.test
## Checking out the source code

To clone the repository in your environment:

```shell
git clone --recurse-submodules -b master -j8 git@github.com:snackwangpetter/Elastos.DID.Android.test.git
```

We use --recurse-submodules here because we need to download the submodules as well. If you forgot to use the argument, you could download the submodules later by typing:

```shell
git submodule update --init --recursive
```

To update submodules with the lastest changes, type:

```shell
git submodule update --rebase --remote
```

And then use git add, commit and push to submit your changes to current project.
