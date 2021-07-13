<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-md w-full space-y-8">
      <div>
        <img class="mx-auto h-12 w-auto" src="https://tailwindui.com/img/logos/workflow-mark-indigo-600.svg"
             alt="Workflow">
        <h2 class="mt-6 text-center text-3xl font-extrabold text-gray-900">
          Sign up your own account
        </h2>
      </div>
      <form class="mt-8 space-y-6" action="#" novalidate>
        <div class="shadow overflow-hidden sm:rounded-md">
          <div class="px-4 py-5 bg-white sm:p-6">
            <div class="grid grid-cols-6 gap-6">

              <div class="col-span-6">
                <p> invalid {{validations.registerAccount.username.$invalid}}</p>
                <p> model {{validations.registerAccount.username.$model}}</p>
                <p> error {{validations.registerAccount.username.$error}}</p>
                <p> params {{validations.registerAccount.username.$params}}</p>
                <p> dirty {{validations.registerAccount.username.$dirty}}</p>
                <p> required {{validations.registerAccount.username.required}}</p>
                <p> minLength {{validations.registerAccount.username.minLength}}</p>
                <p> maxLength {{validations.registerAccount.username.maxLength}}</p>
                <p> alphaNum {{validations.registerAccount.username.alphaNum}}</p>
                <label for="username"
                       :class="{ valid: !validations.registerAccount.username.$invalid, invalid: validations.registerAccount.username.$invalid }"
                       class="block text-sm font-medium text-gray-700">Username</label>
                <input v-model="validations.registerAccount.username.$model" type="text" name="username" id="username"
                       class="mt-1 focus:ring-indigo-500 focus:border-indigo-500 block w-full shadow-sm sm:text-sm border-gray-300 rounded-md">
              </div>

              <div class="col-span-6 sm:col-span-3">
                <label for="first-name" class="block text-sm font-medium text-gray-700">First name</label>
                <input type="text" name="first-name" id="first-name"
                       class="mt-1 focus:ring-indigo-500 focus:border-indigo-500 block w-full shadow-sm sm:text-sm border-gray-300 rounded-md">
              </div>

              <div class="col-span-6 sm:col-span-3">
                <label for="last-name" class="block text-sm font-medium text-gray-700">Last name</label>
                <input type="text" name="last-name" id="last-name"
                       class="mt-1 focus:ring-indigo-500 focus:border-indigo-500 block w-full shadow-sm sm:text-sm border-gray-300 rounded-md">
              </div>

              <div class="col-span-6">
                <label for="email" class="block text-sm font-medium text-gray-700">Email Address</label>
                <input type="text" name="email" id="email"
                       class="mt-1 focus:ring-indigo-500 focus:border-indigo-500 block w-full shadow-sm sm:text-sm border-gray-300 rounded-md">
              </div>

              <div class="col-span-6">
                <label for="password" class="block text-sm font-medium text-gray-700">Password</label>
                <input type="password" name="password" id="password"
                       class="mt-1 focus:ring-indigo-500 focus:border-indigo-500 block w-full shadow-sm sm:text-sm border-gray-300 rounded-md">
              </div>

              <div class="col-span-6">
                <label for="confirm-password" class="block text-sm font-medium text-gray-700">Confirm Password</label>
                <input type="password" name="confirm-password" id="confirm-password"
                       class="mt-1 focus:ring-indigo-500 focus:border-indigo-500 block w-full shadow-sm sm:text-sm border-gray-300 rounded-md">
              </div>

            </div>
          </div>
          <div class="px-4 py-3 bg-gray-50 text-right sm:px-6">
            <button v-on:click.prevent="register" type="submit"
                    class="w-full inline-flex justify-center py-2 px-4 border border-transparent shadow-sm text-sm
                     font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none
                      focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500">
              Save
            </button>
          </div>
        </div>
      </form>
      <div class="flex-column items-center">
        <span class="text-gray-900">If you want to <RouterLink class="font-medium text-indigo-600 hover:text-indigo-500"
                                                               to="/">Sing in</RouterLink></span>
        <div class="max-w-7xl mx-auto py-3 px-3 sm:px-4 lg:px-6 bg-indigo-400 my-2 rounded-md">
          <div class="flex items-center justify-between flex-wrap">
            <div class="w-0 flex-1 flex items-center">
              <div class="text-sm px-4 py-3">
                <div class="text-white">
                  <p>you can try the default accounts:</p>
                  <p>- Administrator (login="admin" and password="admin")</p>
                  <p>- User (login="user" and password="user").</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./register.component.ts"></script>

<style lang="scss">
.text-gray-700.invalid {
  color: red;
}

.text-gray-700.valid {
  color: green;
}
</style>