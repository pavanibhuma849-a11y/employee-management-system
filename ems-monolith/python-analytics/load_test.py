import urllib.request
import time
import threading
import json


URL = "http://localhost:8080/employees"
CONCURRENT_REQUESTS = 50
TIMEOUT = 2.0  # seconds

results = []

def send_request(request_id):
    start_time = time.time()
    try:
        with urllib.request.urlopen(URL, timeout=TIMEOUT) as response:
            status = response.getcode()
            duration = time.time() - start_time
            results.append({
                "id": request_id,
                "status": status,
                "duration": duration,
                "success": status == 200
            })
    except Exception as e:
        duration = time.time() - start_time
        results.append({
            "id": request_id,
            "status": "Error",
            "duration": duration,
            "success": False,
            "error": str(e)
        })

def run_load_test():
    print(f"Starting Concurrent API Load Test (TC-050)")
    print(f"Target URL: {URL}")
    print(f"Simulating {CONCURRENT_REQUESTS} simultaneous requests...\n")

    threads = []
    for i in range(CONCURRENT_REQUESTS):
        t = threading.Thread(target=send_request, args=(i,))
        threads.append(t)
        t.start()
        time.sleep(0.01) # Add tiny delay to prevent RuntimeError on some OSs

    for t in threads:
        t.join()

    # Analyze results
    total_requests = len(results)
    successful_requests = sum(1 for r in results if r["success"])
    failed_requests = total_requests - successful_requests
    durations = [r["duration"] for r in results]
    
    avg_duration = sum(durations) / total_requests if total_requests > 0 else 0
    max_duration = max(durations) if durations else 0
    min_duration = min(durations) if durations else 0

    print("--- Load Test Results ---")
    print(f"Total Requests: {total_requests}")
    print(f"Successful: {successful_requests}")
    print(f"Failed: {failed_requests}")
    print(f"Average Duration: {avg_duration:.4f}s")
    print(f"Max Duration: {max_duration:.4f}s")
    print(f"Min Duration: {min_duration:.4f}s")

    # TC-050 Requirement Check: status 200 and duration < 2s
    passed = successful_requests == CONCURRENT_REQUESTS and max_duration < TIMEOUT
    print(f"\nTC-050 Status: {'PASS' if passed else 'FAIL'}")

    # Export results
    if not os.path.exists('reports'):
        os.makedirs('reports')
    with open('reports/load_test_results.json', 'w') as f:
        json.dump({
            "total_requests": total_requests,
            "successful": successful_requests,
            "failed": failed_requests,
            "avg_duration": avg_duration,
            "max_duration": max_duration,
            "min_duration": min_duration,
            "passed": passed,
            "details": results
        }, f, indent=4)
    print(f"Full results saved to reports/load_test_results.json")

if __name__ == "__main__":
    import os
    run_load_test()
